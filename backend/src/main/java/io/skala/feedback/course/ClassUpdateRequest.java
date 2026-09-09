package io.skala.feedback.course;

import java.time.LocalDate;

// AdminCoursesView 인라인 편집(수업방식/실습교수/수업기간)을 필드별 부분 수정으로 받는다 —
// null인 필드는 그대로 둔다. 총원은 여기서 안 받는다 — 실제 회원가입 인원으로 자동 계산.
public record ClassUpdateRequest(
		ClassFormat format,
		Long labInstructorId,
		LocalDate startDate,
		LocalDate endDate) {
}
