package io.skala.feedback.survey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.course.Course;
import io.skala.feedback.course.CourseRepository;
import io.skala.feedback.survey.SurveyBlockDto.ItemDto;
import io.skala.feedback.survey.SurveyBlockDto.TagDto;
import io.skala.feedback.survey.SurveyTemplateGraphDto.EdgeDto;
import io.skala.feedback.survey.SurveyTemplateGraphDto.NodeDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 과정별 커스텀 설문 템플릿 — 저장된 게 없으면 SurveyBlockTemplate의 하드코딩 기본값이
// 그대로 기본값 역할을 한다(blocksForCourse). 조건부 분기는 아직 없어서(survey-branching-logic
// 메모리의 v2 과제) 저장 시점에 "선형 하나의 사슬"만 허용하도록 검증한다.
@Service
@Transactional(readOnly = true)
public class SurveyTemplateService {

	private static final Set<String> KNOWN_TYPES =
			Set.of("score-drag", "card-rank", "quick-tag", "short-answer", "long-answer", "end");

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

	// 저장된 템플릿이 있으면 선형 순서로 변환, 없으면 기존 하드코딩 기본값 그대로 —
	// SurveyService(학생 화면)와 AnalysisService(집계) 둘 다 이걸로 문항을 얻는다.
	public List<SurveyBlockDto> blocksForCourse(Long courseId) {
		return surveyTemplateRepository.findByCourseId(courseId)
				.map(t -> toBlocks(readGraph(t.getTemplateJson())))
				.orElseGet(SurveyBlockTemplate::blocks);
	}

	private List<SurveyBlockDto> toBlocks(SurveyTemplateGraphDto graph) {
		return linearOrder(graph).stream()
				.filter(n -> !"end".equals(n.type()))
				.map(this::toBlockDto)
				.toList();
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

	// 시작 노드(들어오는 edge 없음)에서 출발해 나가는 edge를 하나씩 따라가며 순서를 만든다.
	// 사이클/존재하지 않는 타깃을 만나면 즉시 예외 — validate()와 toBlocks() 둘 다 이걸 쓴다.
	private List<NodeDto> linearOrder(SurveyTemplateGraphDto graph) {
		Map<String, NodeDto> byId = new HashMap<>();
		for (NodeDto n : graph.nodes()) byId.put(n.id(), n);

		Map<String, String> nextByFrom = new HashMap<>();
		Set<String> hasIncoming = new HashSet<>();
		for (EdgeDto e : graph.edges()) {
			nextByFrom.put(e.source(), e.target());
			hasIncoming.add(e.target());
		}

		String startId = graph.nodes().stream()
				.map(NodeDto::id)
				.filter(id -> !hasIncoming.contains(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("시작 블록을 찾을 수 없어요."));

		List<NodeDto> ordered = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		String current = startId;
		while (current != null) {
			if (!visited.add(current)) {
				throw new IllegalArgumentException("설문 구조에 순환이 있어요.");
			}
			NodeDto node = byId.get(current);
			if (node == null) {
				throw new IllegalArgumentException("존재하지 않는 블록을 가리키고 있어요: " + current);
			}
			ordered.add(node);
			current = nextByFrom.get(current);
		}
		return ordered;
	}

	private void validate(SurveyTemplateGraphDto graph) {
		if (graph.nodes() == null || graph.nodes().isEmpty()) {
			throw new IllegalArgumentException("빈 설문은 저장할 수 없어요.");
		}
		for (NodeDto n : graph.nodes()) {
			if (!KNOWN_TYPES.contains(n.type())) {
				throw new IllegalArgumentException("알 수 없는 블록 타입이에요: " + n.type());
			}
		}
		long endCount = graph.nodes().stream().filter(n -> "end".equals(n.type())).count();
		if (endCount != 1) {
			throw new IllegalArgumentException("종료 블록은 정확히 1개여야 해요.");
		}

		// 조건부 분기가 아직 없어서(v1) 각 블록은 다음 블록으로 연결이 하나뿐이어야 한다.
		Map<String, Long> outgoingCount = graph.edges().stream()
				.collect(Collectors.groupingBy(EdgeDto::source, Collectors.counting()));
		for (NodeDto n : graph.nodes()) {
			long out = outgoingCount.getOrDefault(n.id(), 0L);
			if ("end".equals(n.type())) {
				if (out != 0) throw new IllegalArgumentException("종료 블록 뒤에는 아무것도 연결할 수 없어요.");
			} else if (out != 1) {
				throw new IllegalArgumentException(
						"'" + n.id() + "' 블록은 다음 블록으로 연결이 하나만 있어야 해요(분기는 아직 지원하지 않아요).");
			}
		}

		List<NodeDto> ordered = linearOrder(graph);
		if (ordered.size() != graph.nodes().size()) {
			throw new IllegalArgumentException("연결되지 않은 블록이 있어요.");
		}
		if (!"end".equals(ordered.get(ordered.size() - 1).type())) {
			throw new IllegalArgumentException("설문이 종료 블록으로 끝나야 해요.");
		}

		for (NodeDto n : ordered) {
			Map<String, Object> data = n.data() != null ? n.data() : Map.of();
			if ("card-rank".equals(n.type())) {
				List<?> items = (List<?>) data.getOrDefault("items", List.of());
				if (items.size() < 2) throw new IllegalArgumentException("카드순위 블록엔 항목이 최소 2개 있어야 해요.");
			} else if ("quick-tag".equals(n.type())) {
				List<?> tags = (List<?>) data.getOrDefault("tags", List.of());
				if (tags.isEmpty()) throw new IllegalArgumentException("퀵태그 블록엔 태그가 최소 1개 있어야 해요.");
			}
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
