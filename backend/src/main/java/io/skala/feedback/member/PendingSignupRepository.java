package io.skala.feedback.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingSignupRepository extends JpaRepository<PendingSignup, Long> {
}
