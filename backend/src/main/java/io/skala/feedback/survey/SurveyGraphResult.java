package io.skala.feedback.survey;

import io.skala.feedback.survey.SurveyTemplateGraphDto.EdgeDto;
import java.util.List;

// SurveyTemplateService.graphForCourse()의 결과 — 학생 화면이 조건부 분기를 스스로
// 판정할 수 있도록 블록뿐 아니라 엣지(조건 포함)/시작/종료 블록 id까지 그대로 내려준다.
public record SurveyGraphResult(List<SurveyBlockDto> blocks, List<EdgeDto> edges, String startBlockId, String endBlockId) {
}
