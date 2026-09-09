package io.skala.feedback.analysis;

import java.util.List;
import java.util.Map;

// 과정(course) 하나에 대한 실제 설문 응답 집계 결과 — 프론트 대시보드/키워드/갤러리
// 카드가 그대로 소비할 수 있는 모양으로 맞췄다(ScoreBarsCard/RankLeaderboardCard/
// KeywordGraphCard/FeedbackGalleryCard의 prop 계약).
public record CourseAnalysisDto(
		List<ClassScoreDto> classScores,
		Double courseAvgScore,
		int courseRespondentCount,
		Double benchmarkAvgScore,
		int benchmarkRespondentCount,
		Double myLabScore,
		int myLabRespondentCount,
		List<RankItemDto> rankItems,
		KeywordGraphDto keywordGraph,
		Map<String, PositionDto> keywordPositions,
		List<GalleryItemDto> gallery) {

	// avgScore/respondentCount가 0이면 아직 응답이 없다는 뜻 — 가짜 값으로 채우지 않는다.
	public record ClassScoreDto(Long classId, String className, Double avgScore, int respondentCount) {
	}

	// previousAvgRank는 비교할 지난 기간 데이터가 없어서 avgRank와 항상 같게 둔다
	// (변동 없음 "–"으로 표시되게 — cause-diagnosis-logic 메모리, 가짜 트렌드를 만들지 않음).
	public record RankItemDto(String id, String label, double avgRank, double previousAvgRank) {
	}

	public record KeywordGraphDto(List<KeywordNodeDto> nodes, List<KeywordEdgeDto> edges) {
	}

	public record KeywordNodeDto(String id, String label, int weight) {
	}

	public record KeywordEdgeDto(String id, String source, String target, int weight) {
	}

	public record PositionDto(double x, double y) {
	}

	// courseName은 FeedbackGalleryCard의 범용 prop 이름을 그대로 쓴 것 — 실제로는
	// 이 과정 안의 반 이름을 담는다(과정 자체는 이미 화면 헤더로 고정돼 있으니까).
	public record GalleryItemDto(String id, List<String> tags, String summary, String courseName) {
	}
}
