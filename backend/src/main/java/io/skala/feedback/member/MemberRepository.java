package io.skala.feedback.member;

import io.skala.feedback.common.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByRole(Role role);
}
