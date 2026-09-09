package io.skala.feedback.course;

import java.time.LocalDate;

// AdminCoursesView 인라인 편집(총원/수업방식/실습교수/수업기간)을 필드별 부분 수정으로 받는다 —
// null인 필드는 그대로 둔다.
public record ClassUpdateRequest(
		Integer capacity,
		ClassFormat format,
		Long labInstructorId,
		LocalDate startDate,
		LocalDate endDate) {
}
