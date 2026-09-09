package io.skala.feedback.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

// 총원은 더 이상 여기서 받지 않는다 — 실제 회원가입 인원으로 자동 계산된다
// (CourseService.resyncClassHeadcounts).
public record NewClassRequest(
		@NotBlank String name,
		@NotNull ClassFormat format,
		Long labInstructorId,
		LocalDate startDate,
		LocalDate endDate) {
}
