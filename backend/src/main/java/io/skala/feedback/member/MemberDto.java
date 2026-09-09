package io.skala.feedback.member;

import io.skala.feedback.common.Role;

public record MemberDto(Long id, String name, String email, Role role) {

	static MemberDto from(Member member) {
		return new MemberDto(member.getId(), member.getName(), member.getEmail(), member.getRole());
	}
}
