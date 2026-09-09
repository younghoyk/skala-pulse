package io.skala.feedback.auth;

import io.skala.feedback.common.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DemoLoginRequest(@NotNull Role role, @NotBlank String displayName) {
}
