package io.skala.feedback.course;

import jakarta.validation.constraints.NotNull;

public record CourseUpdateRequest(@NotNull Long leadInstructorId) {
}
