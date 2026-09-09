package io.skala.feedback.member;

import io.skala.feedback.common.Role;
import jakarta.validation.constraints.NotNull;

public record RoleChangeRequest(@NotNull Role role) {
}
