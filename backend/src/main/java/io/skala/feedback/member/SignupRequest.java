package io.skala.feedback.member;

import io.skala.feedback.common.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// className/serialNumber는 교육생 신청에서만 채워진다 — 교수자 신청이면 null(서비스에서 검증).
public record SignupRequest(
		@NotBlank String name,
		@NotBlank @Email String email,
		@NotNull Role requestedRole,
		String className,
		String serialNumber) {
}
