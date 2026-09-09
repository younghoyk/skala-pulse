package io.skala.feedback.member;

import io.skala.feedback.common.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	// 교육생만 해당 — 어느 반(1반~5반) 소속인지, 이미 배정된 일련번호. 교수자/관리자는 null.
	@Column(name = "class_name")
	private String className;

	@Column(name = "serial_number")
	private String serialNumber;

	public Member(String name, String email, Role role) {
		this.name = name;
		this.email = email;
		this.role = role;
	}

	public Member(String name, String email, Role role, String className, String serialNumber) {
		this.name = name;
		this.email = email;
		this.role = role;
		this.className = className;
		this.serialNumber = serialNumber;
	}
}
