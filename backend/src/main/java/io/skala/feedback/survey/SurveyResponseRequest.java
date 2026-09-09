package io.skala.feedback.survey;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

// answers는 블록id -> 응답 형태 그대로 받는다(프론트 responses[surveyId] 구조와 동일) —
// 블록 타입마다 응답 모양이 달라서(순위 배열/문자열/숫자) 구조를 강제하지 않고 그대로 보존한다.
public record SurveyResponseRequest(@NotNull Map<String, Object> answers) {
}
