package io.skala.feedback.member;

import io.skala.feedback.common.Role;

public record PendingSignupDto(Long id, String name, String email, Role requestedRole) {

	static PendingSignupDto from(PendingSignup signup) {
		return new PendingSignupDto(signup.getId(), signup.getName(), signup.getEmail(), signup.getRequestedRole());
	}
}
