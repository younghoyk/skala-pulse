package io.skala.feedback.member;

import io.skala.feedback.common.Role;

public record PendingSignupDto(
		Long id, String name, String email, Role requestedRole, String className, String serialNumber) {

	static PendingSignupDto from(PendingSignup signup) {
		return new PendingSignupDto(
				signup.getId(), signup.getName(), signup.getEmail(), signup.getRequestedRole(),
				signup.getClassName(), signup.getSerialNumber());
	}
}
