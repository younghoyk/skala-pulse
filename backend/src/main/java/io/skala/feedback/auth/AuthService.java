package io.skala.feedback.auth;

import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.common.Role;
import io.skala.feedback.member.Member;
import io.skala.feedback.member.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	public AuthService(JwtService jwtService, MemberRepository memberRepository) {
		this.jwtService = jwtService;
		this.memberRepository = memberRepository;
	}

	// 데모 로그인 화면에서 "누구로 로그인할지" 자동으로 고를 후보 목록 — 승인된 회원만.
	public List<DemoStudentDto> listDemoStudents() {
		return listByRole(Role.STUDENT);
	}

	public List<DemoStudentDto> listDemoInstructors() {
		return listByRole(Role.INSTRUCTOR);
	}

	private List<DemoStudentDto> listByRole(Role role) {
		return memberRepository.findAll().stream()
				.filter(m -> m.getRole() == role)
				.map(m -> new DemoStudentDto(m.getId(), m.getName(), m.getClassName(), m.getSerialNumber()))
				.toList();
	}

	public TokenResponse demoLogin(DemoLoginRequest request) {
		// 교육생/교수자는 실제 승인된 회원 하나로 로그인한다(이름/반 정보가 그 회원 걸 그대로
		// 따라옴). 관리자는 지금처럼 특정 회원과 안 묶인 자유 입력 데모 로그인.
		if (request.role() == Role.STUDENT || request.role() == Role.INSTRUCTOR) {
			return loginAsMember(request.role(), request.memberId());
		}

		if (request.displayName() == null || request.displayName().isBlank()) {
			throw new IllegalArgumentException("이름을 입력해주세요.");
		}
		String token = jwtService.issueToken(request.displayName(), request.role(), null, null);
		return new TokenResponse(token, request.role(), request.displayName(), null);
	}

	private TokenResponse loginAsMember(Role role, Long memberId) {
		if (memberId == null) {
			throw new IllegalArgumentException("로그인할 회원을 선택해야 해요.");
		}
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new NotFoundException("회원을 찾을 수 없어요: " + memberId));
		if (member.getRole() != role) {
			throw new IllegalArgumentException("역할이 맞지 않는 회원이에요.");
		}
		String token = jwtService.issueToken(member.getName(), role, member.getClassName(), member.getId());
		return new TokenResponse(token, role, member.getName(), member.getClassName());
	}
}
