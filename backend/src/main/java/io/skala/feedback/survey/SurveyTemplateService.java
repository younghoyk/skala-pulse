package io.skala.feedback.survey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.course.Course;
import io.skala.feedback.course.CourseRepository;
import io.skala.feedback.survey.SurveyBlockDto.ItemDto;
import io.skala.feedback.survey.SurveyBlockDto.TagDto;
import io.skala.feedback.survey.SurveyTemplateGraphDto.ConditionDto;
import io.skala.feedback.survey.SurveyTemplateGraphDto.EdgeDto;
import io.skala.feedback.survey.SurveyTemplateGraphDto.NodeDto;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 과정별 커스텀 설문 템플릿 — 저장된 게 없으면 SurveyBlockTemplate의 하드코딩 기본값이
// 그대로 기본값 역할을 한다(graphForCourse). v2: 조건부 분기 지원 — 그래프가 더 이상
// 직선 사슬일 필요는 없고, DAG(사이클 없음 + 블록마다 조건 없는 기본 엣지 정확히 1개)만
// 만족하면 된다(survey-branching-logic 메모리). 조건 자체의 평가는 프론트에서 하고,
// 여기선 그래프+조건을 그대로 저장/서빙하고 저장 시점에 구조를 검증하기만 한다.
@Service
@Transactional(readOnly = true)
public class SurveyTemplateService {

	private static final Set<String> KNOWN_TYPES =
			Set.of("score-drag", "card-rank", "quick-tag", "short-answer", "long-answer", "end");
	private static final Set<String> BRANCHABLE_TYPES = Set.of("score-drag", "card-rank", "quick-tag");
	private static final Set<String> COMPARATORS = Set.of("gte", "lte", "gt", "lt", "eq");

	private final SurveyTemplateRepository surveyTemplateRepository;
	private final CourseRepository courseRepository;
	private final ObjectMapper objectMapper;

	public SurveyTemplateService(
			SurveyTemplateRepository surveyTemplateRepository,
			CourseRepository courseRepository,
			ObjectMapper objectMapper) {
		this.surveyTemplateRepository = surveyTemplateRepository;
		this.courseRepository = courseRepository;
		this.objectMapper = objectMapper;
	}

	// 저장된 게 없으면 null — 빌더는 이걸 "아직 커스텀 안 됨"으로 보고 로컬 시드 템플릿을 쓴다.
	public SurveyTemplateGraphDto getGraph(Long courseId) {
		return surveyTemplateRepository.findByCourseId(courseId)
				.map(t -> readGraph(t.getTemplateJson()))
				.orElse(null);
	}

	@Transactional
	public void saveGraph(Long courseId, SurveyTemplateGraphDto graph) {
		validate(graph);
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("과정을 찾을 수 없어요: " + courseId));
		String json = writeGraph(graph);
		SurveyTemplate template = surveyTemplateRepository.findByCourseId(courseId)
				.orElseGet(() -> new SurveyTemplate(course, json));
		template.setTemplateJson(json);
		template.setUpdatedAt(Instant.now());
		surveyTemplateRepository.save(template);
	}

	// 저장된 템플릿이 있으면 그걸(조건부 엣지 포함), 없으면 기존 하드코딩 기본값을 무조건
	// 엣지 체인으로 합성해서 반환 — SurveyService(학생 화면)가 이걸로 전체 그래프를 서빙한다.
	public SurveyGraphResult graphForCourse(Long courseId) {
		return surveyTemplateRepository.findByCourseId(courseId)
				.map(t -> toGraphResult(readGraph(t.getTemplateJson())))
				.orElseGet(this::defaultGraph);
	}

	// AnalysisService처럼 블록 내용(라벨 등)만 필요한 호출부용 — 엣지는 필요 없다.
	public List<SurveyBlockDto> blocksForCourse(Long courseId) {
		return graphForCourse(courseId).blocks();
	}

	private SurveyGraphResult defaultGraph() {
		List<SurveyBlockDto> blocks = SurveyBlockTemplate.blocks();
		List<EdgeDto> edges = new ArrayList<>();
		for (int i = 0; i < blocks.size() - 1; i++) {
			edges.add(new EdgeDto("e-" + i, blocks.get(i).id(), blocks.get(i + 1).id(), null));
		}
		String endId = "end";
		edges.add(new EdgeDto("e-end", blocks.get(blocks.size() - 1).id(), endId, null));
		return new SurveyGraphResult(blocks, edges, blocks.get(0).id(), endId);
	}

	private SurveyGraphResult toGraphResult(SurveyTemplateGraphDto graph) {
		NodeDto endNode = graph.nodes().stream()
				.filter(n -> "end".equals(n.type()))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("저장된 템플릿에 종료 블록이 없어요."));
		String startId = findStart(graph);
		List<SurveyBlockDto> blocks = graph.nodes().stream()
				.filter(n -> !"end".equals(n.type()))
				.map(this::toBlockDto)
				.toList();
		return new SurveyGraphResult(blocks, graph.edges(), startId, endNode.id());
	}

	private String findStart(SurveyTemplateGraphDto graph) {
		Set<String> hasIncoming = new java.util.HashSet<>();
		for (EdgeDto e : graph.edges()) hasIncoming.add(e.target());
		return graph.nodes().stream()
				.map(NodeDto::id)
				.filter(id -> !hasIncoming.contains(id))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("저장된 템플릿에 시작 블록이 없어요."));
	}

	private SurveyBlockDto toBlockDto(NodeDto node) {
		Map<String, Object> data = node.data() != null ? node.data() : Map.of();
		String question = String.valueOf(data.getOrDefault("question", ""));
		List<ItemDto> items = null;
		List<TagDto> tags = null;
		Integer summaryMaxLength = null;
		Integer min = null;
		Integer max = null;
		if ("card-rank".equals(node.type())) {
			items = objectMapper.convertValue(
					data.getOrDefault("items", List.of()), new TypeReference<List<ItemDto>>() {});
		} else if ("quick-tag".equals(node.type())) {
			tags = objectMapper.convertValue(
					data.getOrDefault("tags", List.of()), new TypeReference<List<TagDto>>() {});
			summaryMaxLength = 100;
		} else if ("score-drag".equals(node.type())) {
			// 빌더는 아직 min/max를 편집하는 UI가 없어서 항상 0~5 고정.
			min = 0;
			max = 5;
		}
		return new SurveyBlockDto(node.id(), node.type(), question, items, tags, summaryMaxLength, min, max);
	}

	// 그래프가 유효한 DAG인지 검증한다: 노드/타입 유일성, 종료 블록 정확히 1개, 블록마다
	// 조건 없는 기본 엣지 정확히 1개, 조건부 엣지는 분기 가능한 타입(점수드래그/카드순위/
	// 퀵태그)에서만 + 그 블록의 실제 항목/태그를 가리켜야 함, 그리고 위상정렬(Kahn)로
	// 시작 블록 유일 + 사이클 없음 + 전부 도달 가능을 한 번에 검증한다.
	private void validate(SurveyTemplateGraphDto graph) {
		if (graph.nodes() == null || graph.nodes().isEmpty()) {
			throw new IllegalArgumentException("빈 설문은 저장할 수 없어요.");
		}
		Map<String, NodeDto> byId = new HashMap<>();
		for (NodeDto n : graph.nodes()) {
			if (!KNOWN_TYPES.contains(n.type())) {
				throw new IllegalArgumentException("알 수 없는 블록 타입이에요: " + n.type());
			}
			if (byId.put(n.id(), n) != null) {
				throw new IllegalArgumentException("블록 id가 중복됐어요: " + n.id());
			}
		}
		long endCount = graph.nodes().stream().filter(n -> "end".equals(n.type())).count();
		if (endCount != 1) {
			throw new IllegalArgumentException("종료 블록은 정확히 1개여야 해요.");
		}

		Map<String, List<EdgeDto>> outgoingByNode = new LinkedHashMap<>();
		for (NodeDto n : graph.nodes()) outgoingByNode.put(n.id(), new ArrayList<>());
		for (EdgeDto e : graph.edges()) {
			if (!byId.containsKey(e.source())) {
				throw new IllegalArgumentException("존재하지 않는 블록에서 시작하는 연결이 있어요: " + e.source());
			}
			if (!byId.containsKey(e.target())) {
				throw new IllegalArgumentException("존재하지 않는 블록을 가리키는 연결이 있어요: " + e.target());
			}
			outgoingByNode.get(e.source()).add(e);
		}

		for (NodeDto n : graph.nodes()) {
			List<EdgeDto> outgoing = outgoingByNode.get(n.id());
			if ("end".equals(n.type())) {
				if (!outgoing.isEmpty()) {
					throw new IllegalArgumentException("종료 블록 뒤에는 아무것도 연결할 수 없어요.");
				}
				continue;
			}
			long defaultCount = outgoing.stream().filter(e -> e.condition() == null).count();
			if (defaultCount != 1) {
				throw new IllegalArgumentException("'" + n.id() + "' 블록엔 조건 없는 기본 연결이 정확히 1개 있어야 해요.");
			}
			for (EdgeDto e : outgoing) {
				if (e.condition() == null) continue;
				if (!BRANCHABLE_TYPES.contains(n.type())) {
					throw new IllegalArgumentException("'" + n.id() + "' 블록은 조건부 분기를 지원하지 않아요.");
				}
				validateCondition(n, e.condition());
			}
		}

		Map<String, Integer> indegree = new HashMap<>();
		for (NodeDto n : graph.nodes()) indegree.put(n.id(), 0);
		for (EdgeDto e : graph.edges()) indegree.merge(e.target(), 1, Integer::sum);

		List<String> starts = indegree.entrySet().stream()
				.filter(e -> e.getValue() == 0)
				.map(Map.Entry::getKey)
				.toList();
		if (starts.size() != 1) {
			throw new IllegalArgumentException("시작 블록은 정확히 1개여야 해요.");
		}

		Deque<String> queue = new ArrayDeque<>(starts);
		Map<String, Integer> remaining = new HashMap<>(indegree);
		int processed = 0;
		while (!queue.isEmpty()) {
			String id = queue.poll();
			processed++;
			for (EdgeDto e : outgoingByNode.get(id)) {
				int left = remaining.merge(e.target(), -1, Integer::sum);
				if (left == 0) queue.add(e.target());
			}
		}
		if (processed != graph.nodes().size()) {
			throw new IllegalArgumentException("설문 구조에 순환이 있거나 연결되지 않은 블록이 있어요.");
		}
	}

	private void validateCondition(NodeDto sourceNode, ConditionDto c) {
		if (c.type() == null || !c.type().equals(sourceNode.type())) {
			throw new IllegalArgumentException("'" + sourceNode.id() + "' 블록의 조건 타입이 올바르지 않아요.");
		}
		Map<String, Object> data = sourceNode.data() != null ? sourceNode.data() : Map.of();
		switch (sourceNode.type()) {
			case "score-drag" -> {
				if (c.comparator() == null || !COMPARATORS.contains(c.comparator()) || c.value() == null) {
					throw new IllegalArgumentException("'" + sourceNode.id() + "' 블록의 점수 조건이 올바르지 않아요.");
				}
			}
			case "card-rank" -> {
				List<?> items = (List<?>) data.getOrDefault("items", List.of());
				List<String> itemIds = items.stream().map(i -> String.valueOf(((Map<?, ?>) i).get("id"))).toList();
				if (c.itemId() == null || !itemIds.contains(c.itemId())
						|| c.minPosition() == null || c.maxPosition() == null
						|| c.minPosition() < 1 || c.maxPosition() > itemIds.size() || c.minPosition() > c.maxPosition()) {
					throw new IllegalArgumentException("'" + sourceNode.id() + "' 블록의 순위 조건이 올바르지 않아요.");
				}
			}
			case "quick-tag" -> {
				List<?> tags = (List<?>) data.getOrDefault("tags", List.of());
				List<String> tagIds = tags.stream().map(t -> String.valueOf(((Map<?, ?>) t).get("id"))).toList();
				if (c.tagId() == null || !tagIds.contains(c.tagId())) {
					throw new IllegalArgumentException("'" + sourceNode.id() + "' 블록의 태그 조건이 올바르지 않아요.");
				}
			}
			default -> throw new IllegalArgumentException("이 블록 타입은 조건을 가질 수 없어요.");
		}
	}

	private SurveyTemplateGraphDto readGraph(String json) {
		try {
			return objectMapper.readValue(json, SurveyTemplateGraphDto.class);
		} catch (Exception e) {
			throw new IllegalStateException("저장된 설문 템플릿을 읽을 수 없어요.", e);
		}
	}

	private String writeGraph(SurveyTemplateGraphDto graph) {
		try {
			return objectMapper.writeValueAsString(graph);
		} catch (Exception e) {
			throw new IllegalArgumentException("설문 템플릿을 저장할 수 없어요.");
		}
	}
}
