package io.skala.feedback.analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.skala.feedback.analysis.CourseAnalysisDto.ClassScoreDto;
import io.skala.feedback.analysis.CourseAnalysisDto.GalleryItemDto;
import io.skala.feedback.analysis.CourseAnalysisDto.KeywordEdgeDto;
import io.skala.feedback.analysis.CourseAnalysisDto.KeywordGraphDto;
import io.skala.feedback.analysis.CourseAnalysisDto.KeywordNodeDto;
import io.skala.feedback.analysis.CourseAnalysisDto.PositionDto;
import io.skala.feedback.analysis.CourseAnalysisDto.RankItemDto;
import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.course.ClassGroup;
import io.skala.feedback.course.Course;
import io.skala.feedback.course.CourseRepository;
import io.skala.feedback.survey.SurveyBlockDto;
import io.skala.feedback.survey.SurveyBlockTemplate;
import io.skala.feedback.survey.SurveyRepository;
import io.skala.feedback.survey.SurveyResponse;
import io.skala.feedback.survey.SurveyResponseRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 실제 survey_response를 읽어서 강사 대시보드/키워드/갤러리에 필요한 값을 계산한다.
// 규모가 아직 작아서(교육생 응답 전체) 전량 스캔 후 이 과정 것만 걸러내는 방식으로도
// 충분히 빠르다 — CourseService.myCourses와 같은 패턴.
@Service
@Transactional(readOnly = true)
public class AnalysisService {

	private static final double CIRCLE_RADIUS = 150;
	private static final double CIRCLE_CENTER = 200;

	private final CourseRepository courseRepository;
	private final SurveyRepository surveyRepository;
	private final SurveyResponseRepository surveyResponseRepository;
	private final ObjectMapper objectMapper;

	public AnalysisService(
			CourseRepository courseRepository,
			SurveyRepository surveyRepository,
			SurveyResponseRepository surveyResponseRepository,
			ObjectMapper objectMapper) {
		this.courseRepository = courseRepository;
		this.surveyRepository = surveyRepository;
		this.surveyResponseRepository = surveyResponseRepository;
		this.objectMapper = objectMapper;
	}

	public CourseAnalysisDto analyzeCourse(Long courseId, Long viewerMemberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("과정을 찾을 수 없어요: " + courseId));

		Map<Long, Long> surveyIdToCourseId = new LinkedHashMap<>();
		Map<Long, Long> surveyIdToClassId = new LinkedHashMap<>();
		for (var survey : surveyRepository.findAll()) {
			surveyIdToCourseId.put(survey.getId(), survey.getClassGroup().getCourse().getId());
			surveyIdToClassId.put(survey.getId(), survey.getClassGroup().getId());
		}

		List<ParsedResponse> all = new ArrayList<>();
		for (SurveyResponse response : surveyResponseRepository.findAll()) {
			Long surveyId = response.getSurvey().getId();
			Long responseCourseId = surveyIdToCourseId.get(surveyId);
			Long responseClassId = surveyIdToClassId.get(surveyId);
			if (responseCourseId == null) continue;
			ParsedResponse parsed = parse(response, responseCourseId, responseClassId);
			if (parsed != null) all.add(parsed);
		}

		List<ParsedResponse> inCourse = all.stream().filter(p -> p.courseId.equals(courseId)).toList();
		List<ParsedResponse> outsideCourse = all.stream().filter(p -> !p.courseId.equals(courseId)).toList();

		List<ClassScoreDto> classScores = course.getClasses().stream()
				.map(cls -> toClassScore(cls, inCourse))
				.toList();

		Double courseAvg = average(inCourse, p -> p.score);
		int courseCount = (int) inCourse.stream().filter(p -> p.score != null).count();

		Double benchmarkAvg = average(outsideCourse, p -> p.score);
		int benchmarkCount = (int) outsideCourse.stream().filter(p -> p.score != null).count();

		ClassGroup myLabClass = course.getClasses().stream()
				.filter(cls -> cls.getLabInstructor() != null
						&& cls.getLabInstructor().getId().equals(viewerMemberId))
				.findFirst()
				.orElse(null);
		Double myLabScore = null;
		int myLabRespondentCount = 0;
		if (myLabClass != null) {
			List<ParsedResponse> inMyLabClass = inCourse.stream()
					.filter(p -> p.classId.equals(myLabClass.getId()))
					.toList();
			myLabScore = average(inMyLabClass, p -> p.labScore);
			myLabRespondentCount = (int) inMyLabClass.stream().filter(p -> p.labScore != null).count();
		}

		List<RankItemDto> rankItems = buildRankItems(inCourse);
		KeywordGraphDto keywordGraph = buildKeywordGraph(inCourse);
		Map<String, PositionDto> positions = buildPositions(keywordGraph);
		List<GalleryItemDto> gallery = buildGallery(inCourse, course.getClasses());

		return new CourseAnalysisDto(
				course.getName(), classScores, courseAvg, courseCount, benchmarkAvg, benchmarkCount,
				myLabScore, myLabRespondentCount, rankItems, keywordGraph, positions, gallery);
	}

	private ClassScoreDto toClassScore(ClassGroup cls, List<ParsedResponse> inCourse) {
		List<ParsedResponse> inClass = inCourse.stream().filter(p -> p.classId.equals(cls.getId())).toList();
		Double avg = average(inClass, p -> p.score);
		int count = (int) inClass.stream().filter(p -> p.score != null).count();
		return new ClassScoreDto(cls.getId(), cls.getName(), avg, count);
	}

	private List<RankItemDto> buildRankItems(List<ParsedResponse> inCourse) {
		Map<String, String> itemLabels = itemLabels();
		Map<String, Double> rankSums = new LinkedHashMap<>();
		Map<String, Integer> rankCounts = new LinkedHashMap<>();
		for (ParsedResponse p : inCourse) {
			if (p.rank == null) continue;
			for (int i = 0; i < p.rank.size(); i++) {
				String itemId = p.rank.get(i);
				if (!itemLabels.containsKey(itemId)) continue;
				rankSums.merge(itemId, (double) (i + 1), Double::sum);
				rankCounts.merge(itemId, 1, Integer::sum);
			}
		}
		if (rankCounts.isEmpty()) return List.of();
		return itemLabels.entrySet().stream()
				.filter(e -> rankCounts.containsKey(e.getKey()))
				.map(e -> {
					double avgRank = rankSums.get(e.getKey()) / rankCounts.get(e.getKey());
					return new RankItemDto(e.getKey(), e.getValue(), avgRank, avgRank);
				})
				.toList();
	}

	private KeywordGraphDto buildKeywordGraph(List<ParsedResponse> inCourse) {
		Map<String, String> tagLabels = tagLabels();
		Map<String, Integer> nodeCounts = new LinkedHashMap<>();
		Map<String, Integer> edgeCounts = new LinkedHashMap<>();

		for (ParsedResponse p : inCourse) {
			if (p.tagIds == null) continue;
			List<String> tags = p.tagIds.stream().filter(tagLabels::containsKey).distinct().toList();
			for (String tagId : tags) {
				nodeCounts.merge(tagId, 1, Integer::sum);
			}
			for (int i = 0; i < tags.size(); i++) {
				for (int j = i + 1; j < tags.size(); j++) {
					String key = tags.get(i).compareTo(tags.get(j)) < 0
							? tags.get(i) + "|" + tags.get(j)
							: tags.get(j) + "|" + tags.get(i);
					edgeCounts.merge(key, 1, Integer::sum);
				}
			}
		}

		List<KeywordNodeDto> nodes = nodeCounts.entrySet().stream()
				.map(e -> new KeywordNodeDto(e.getKey(), tagLabels.get(e.getKey()), e.getValue()))
				.toList();
		List<KeywordEdgeDto> edges = edgeCounts.entrySet().stream()
				.map(e -> {
					String[] parts = e.getKey().split("\\|");
					return new KeywordEdgeDto(e.getKey(), parts[0], parts[1], e.getValue());
				})
				.toList();
		return new KeywordGraphDto(nodes, edges);
	}

	private Map<String, PositionDto> buildPositions(KeywordGraphDto graph) {
		List<KeywordNodeDto> sorted = graph.nodes().stream()
				.sorted(Comparator.comparingInt(KeywordNodeDto::weight).reversed())
				.toList();
		Map<String, PositionDto> positions = new LinkedHashMap<>();
		int n = sorted.size();
		for (int i = 0; i < n; i++) {
			double angle = 2 * Math.PI * i / n;
			double x = CIRCLE_CENTER + CIRCLE_RADIUS * Math.cos(angle);
			double y = CIRCLE_CENTER + CIRCLE_RADIUS * Math.sin(angle);
			positions.put(sorted.get(i).id(), new PositionDto(x, y));
		}
		return positions;
	}

	private List<GalleryItemDto> buildGallery(List<ParsedResponse> inCourse, List<ClassGroup> classes) {
		Map<String, String> tagLabels = tagLabels();
		Map<Long, String> classNames = new LinkedHashMap<>();
		for (ClassGroup cls : classes) classNames.put(cls.getId(), cls.getName());

		return inCourse.stream()
				.filter(p -> p.summary != null && !p.summary.isBlank())
				.sorted(Comparator.comparing((ParsedResponse p) -> p.submittedAt).reversed())
				.map(p -> {
					List<String> tags = p.tagIds == null
							? List.of()
							: p.tagIds.stream().filter(tagLabels::containsKey).map(tagLabels::get).toList();
					return new GalleryItemDto(
							String.valueOf(p.responseId), tags, p.summary, classNames.get(p.classId));
				})
				.toList();
	}

	private Map<String, String> itemLabels() {
		return blockOf("rank").items().stream()
				.collect(LinkedHashMap::new, (m, i) -> m.put(i.id(), i.label()), Map::putAll);
	}

	private Map<String, String> tagLabels() {
		return blockOf("tag").tags().stream()
				.collect(LinkedHashMap::new, (m, t) -> m.put(t.id(), t.label()), Map::putAll);
	}

	private SurveyBlockDto blockOf(String blockId) {
		return SurveyBlockTemplate.blocks().stream()
				.filter(b -> b.id().equals(blockId))
				.findFirst()
				.orElseThrow();
	}

	private Double average(List<ParsedResponse> list, java.util.function.Function<ParsedResponse, Double> f) {
		OptionalDouble avg = list.stream().map(f).filter(v -> v != null).mapToDouble(Double::doubleValue).average();
		return avg.isPresent() ? avg.getAsDouble() : null;
	}

	private ParsedResponse parse(SurveyResponse response, Long courseId, Long classId) {
		try {
			Map<String, Object> answers = objectMapper.readValue(
					response.getAnswersJson(), new TypeReference<Map<String, Object>>() {});

			Double score = asDouble(answers.get("score"));
			Double labScore = asDouble(answers.get("score-lab"));

			List<String> rank = null;
			Object rankRaw = answers.get("rank");
			if (rankRaw instanceof List<?> list) {
				rank = list.stream().map(String::valueOf).toList();
			}

			List<String> tagIds = null;
			String summary = null;
			Object tagRaw = answers.get("tag");
			if (tagRaw instanceof Map<?, ?> map) {
				Object tagIdsRaw = map.get("tagIds");
				if (tagIdsRaw instanceof List<?> list) {
					tagIds = list.stream().map(String::valueOf).toList();
				}
				Object summaryRaw = map.get("summary");
				if (summaryRaw != null) summary = String.valueOf(summaryRaw);
			}

			return new ParsedResponse(
					response.getId(), classId, courseId, response.getSubmittedAt(),
					score, labScore, rank, tagIds, summary);
		} catch (Exception e) {
			return null;
		}
	}

	private Double asDouble(Object value) {
		return value instanceof Number number ? number.doubleValue() : null;
	}

	private record ParsedResponse(
			Long responseId,
			Long classId,
			Long courseId,
			Instant submittedAt,
			Double score,
			Double labScore,
			List<String> rank,
			List<String> tagIds,
			String summary) {
	}
}
