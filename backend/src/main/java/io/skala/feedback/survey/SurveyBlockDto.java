package io.skala.feedback.survey;

import java.util.List;

// 프론트 mockSurveys.js의 block 모양을 그대로 반영 — type에 따라 items/tags/min/max 중
// 관련 있는 필드만 채워진다(CardRankBlock/QuickTagBlock/ScoreDragBlock.vue가 그대로 읽는다).
public record SurveyBlockDto(
		String id,
		String type,
		String question,
		List<ItemDto> items,
		List<TagDto> tags,
		Integer summaryMaxLength,
		Integer min,
		Integer max) {

	public record ItemDto(String id, String label) {
	}

	public record TagDto(String id, String label, String placeholderHint) {
	}
}
