package io.skala.feedback.auth;

import io.skala.feedback.common.Role;

// JWT의 Authentication principal — memberId가 있으면 실제 회원과 묶인 로그인(교육생/교수자),
// 없으면 관리자처럼 특정 회원과 안 묶인 자유 데모 로그인.
public record AuthenticatedUser(Long memberId, String displayName, Role role) {
}
