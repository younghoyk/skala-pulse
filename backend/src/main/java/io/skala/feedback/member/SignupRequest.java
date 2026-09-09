package io.skala.feedback.member;

import io.skala.feedback.common.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(@NotBlank String name, @NotBlank @Email String email, @NotNull Role requestedRole) {
}
