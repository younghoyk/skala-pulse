package io.skala.feedback.survey;

import java.util.List;
import java.util.Map;

// 관리자 설문 빌더 캔버스가 쓰는 그래프 모양 그대로(구 mockSurveyTemplatesByCourse와
// 같은 shape) — 빌더 ↔ 저장 데이터가 1:1이라 프론트가 변환 없이 그대로 주고받는다.
// data는 블록 타입에 따라 question/items/tags 중 관련 있는 키만 채워진다.
public record SurveyTemplateGraphDto(List<NodeDto> nodes, List<EdgeDto> edges) {

	public record NodeDto(String id, String type, Object position, Map<String, Object> data) {
	}

	public record EdgeDto(String id, String source, String target) {
	}
}
