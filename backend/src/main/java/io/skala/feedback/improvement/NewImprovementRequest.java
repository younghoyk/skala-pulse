package io.skala.feedback.improvement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewImprovementRequest(@NotNull Long courseId, @NotBlank String title) {
}
