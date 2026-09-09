package io.skala.feedback.survey;

import java.util.List;
import java.util.Map;

// 관리자 설문 빌더 캔버스가 쓰는 그래프 모양 그대로(구 mockSurveyTemplatesByCourse와
// 같은 shape) — 빌더 ↔ 저장 데이터가 1:1이라 프론트가 변환 없이 그대로 주고받는다.
// data는 블록 타입에 따라 question/items/tags 중 관련 있는 키만 채워진다.
public record SurveyTemplateGraphDto(List<NodeDto> nodes, List<EdgeDto> edges) {

	public record NodeDto(String id, String type, Object position, Map<String, Object> data) {
	}

	// condition이 null이면 "기본(그 외)" 엣지 — 조건부 엣지들을 배열 순서대로(위→아래) 평가해
	// 첫 매치를 taken, 아무것도 안 맞으면 기본 엣지로 진행한다(survey-branching-logic 메모리).
	public record EdgeDto(String id, String source, String target, ConditionDto condition) {
	}

	// type은 출발 노드의 블록 타입과 같은 문자열을 재사용한다(score-drag/card-rank/quick-tag)
	// — 새 어휘를 안 만들고, 타입별로 관련 있는 필드만 채워진다:
	//   score-drag  -> comparator(gte/lte/gt/lt/eq) + value
	//   card-rank   -> itemId + minPosition~maxPosition(1부터, 그 블록 항목 수까지)
	//   quick-tag   -> tagId(선택 여부만 봄)
	// 단답형/서술형은 조건을 가질 수 없다(자유 서술은 분기 기준이 될 수 없음).
	public record ConditionDto(
			String type, String comparator, Double value, String itemId, Integer minPosition, Integer maxPosition,
			String tagId) {
	}
}
