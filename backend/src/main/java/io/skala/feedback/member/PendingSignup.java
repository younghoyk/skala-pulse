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
@Table(name = "pending_signups")
@Getter
@Setter
@NoArgsConstructor
public class PendingSignup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "requested_role", nullable = false)
	private Role requestedRole;

	@Column(name = "class_name")
	private String className;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "password_hash")
	private String passwordHash;

	public PendingSignup(
			String name, String email, Role requestedRole, String className, String serialNumber,
			String passwordHash) {
		this.name = name;
		this.email = email;
		this.requestedRole = requestedRole;
		this.className = className;
		this.serialNumber = serialNumber;
		this.passwordHash = passwordHash;
	}
}
