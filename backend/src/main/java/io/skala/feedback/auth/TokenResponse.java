package io.skala.feedback.auth;

import io.skala.feedback.common.Role;

public record TokenResponse(String token, Role role, String displayName, String className) {
}
