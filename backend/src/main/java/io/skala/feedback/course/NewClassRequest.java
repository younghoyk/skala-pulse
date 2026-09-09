package io.skala.feedback.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record NewClassRequest(
		@NotBlank String name,
		@NotNull @Positive Integer capacity,
		@NotNull ClassFormat format,
		Long labInstructorId,
		LocalDate startDate,
		LocalDate endDate) {
}
