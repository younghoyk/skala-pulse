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

	// 데모 학생 로그인 화면에서 "누구로 로그인할지" 고를 목록 — 승인된 교육생만.
	public List<DemoStudentDto> listDemoStudents() {
		return memberRepository.findAll().stream()
				.filter(m -> m.getRole() == Role.STUDENT)
				.map(m -> new DemoStudentDto(m.getId(), m.getName(), m.getClassName(), m.getSerialNumber()))
				.toList();
	}

	public TokenResponse demoLogin(DemoLoginRequest request) {
		// 교육생은 실제 승인된 회원 하나로 로그인한다 — 반 정보가 그 회원 걸 그대로 따라오게.
		// 교수자/관리자는 지금처럼 특정 회원과 안 묶인 자유 입력 데모 로그인.
		if (request.role() == Role.STUDENT) {
			if (request.memberId() == null) {
				throw new IllegalArgumentException("교육생 로그인은 회원을 선택해야 해요.");
			}
			Member member = memberRepository.findById(request.memberId())
					.orElseThrow(() -> new NotFoundException("회원을 찾을 수 없어요: " + request.memberId()));
			if (member.getRole() != Role.STUDENT) {
				throw new IllegalArgumentException("교육생 회원이 아니에요.");
			}
			String token = jwtService.issueToken(member.getName(), Role.STUDENT, member.getClassName());
			return new TokenResponse(token, Role.STUDENT, member.getName(), member.getClassName());
		}

		if (request.displayName() == null || request.displayName().isBlank()) {
			throw new IllegalArgumentException("이름을 입력해주세요.");
		}
		String token = jwtService.issueToken(request.displayName(), request.role(), null);
		return new TokenResponse(token, request.role(), request.displayName(), null);
	}
}
