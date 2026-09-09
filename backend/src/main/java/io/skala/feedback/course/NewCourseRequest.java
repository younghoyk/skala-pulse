package io.skala.feedback.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewCourseRequest(@NotBlank String name, @NotNull Long leadInstructorId) {
}
