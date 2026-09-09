package io.skala.feedback.auth;

import io.skala.feedback.common.Role;
import jakarta.validation.constraints.NotNull;

// STUDENT면 memberId가 필수 — 실제 승인된 교육생 회원으로 로그인해서 반 정보가
// 그 회원 것을 그대로 따라오게 한다(displayName은 그 경우 무시). instructor/admin은
// 지금처럼 memberId 없이 자유 입력.
public record DemoLoginRequest(@NotNull Role role, String displayName, Long memberId) {
}
