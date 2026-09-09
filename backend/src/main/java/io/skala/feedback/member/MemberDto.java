package io.skala.feedback.member;

import io.skala.feedback.common.Role;

public record MemberDto(Long id, String name, String email, Role role, String className, String serialNumber) {

	static MemberDto from(Member member) {
		return new MemberDto(
				member.getId(), member.getName(), member.getEmail(), member.getRole(),
				member.getClassName(), member.getSerialNumber());
	}
}
