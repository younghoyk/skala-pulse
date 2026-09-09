package io.skala.feedback.member;

import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.common.Role;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final PendingSignupRepository pendingSignupRepository;

	public MemberService(MemberRepository memberRepository, PendingSignupRepository pendingSignupRepository) {
		this.memberRepository = memberRepository;
		this.pendingSignupRepository = pendingSignupRepository;
	}

	public List<MemberDto> listMembers() {
		return memberRepository.findAll().stream().map(MemberDto::from).toList();
	}

	public List<PendingSignupDto> listPending() {
		return pendingSignupRepository.findAll().stream().map(PendingSignupDto::from).toList();
	}

	@Transactional
	public PendingSignupDto requestSignup(SignupRequest request) {
		if (request.requestedRole() == Role.ADMIN) {
			throw new IllegalArgumentException("관리자 역할은 가입 신청으로 받을 수 없어요.");
		}
		// 교육생은 실제로 반 하나에 소속돼있고 이미 배정된 일련번호가 있다 — 필수로 받는다.
		// 교수자는 특정 반 소속이 아니라(여러 반을 겸함) 해당 없음, 값이 와도 저장하지 않는다.
		boolean isStudent = request.requestedRole() == Role.STUDENT;
		if (isStudent && (isBlank(request.className()) || isBlank(request.serialNumber()))) {
			throw new IllegalArgumentException("교육생은 반과 일련번호를 입력해야 해요.");
		}
		PendingSignup signup = new PendingSignup(
				request.name(), request.email(), request.requestedRole(),
				isStudent ? request.className() : null,
				isStudent ? request.serialNumber() : null);
		return PendingSignupDto.from(pendingSignupRepository.save(signup));
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	@Transactional
	public MemberDto approve(Long signupId) {
		PendingSignup signup = pendingSignupRepository.findById(signupId)
				.orElseThrow(() -> new NotFoundException("가입 신청을 찾을 수 없어요: " + signupId));
		Member member = new Member(
				signup.getName(), signup.getEmail(), signup.getRequestedRole(),
				signup.getClassName(), signup.getSerialNumber());
		Member saved = memberRepository.save(member);
		pendingSignupRepository.delete(signup);
		return MemberDto.from(saved);
	}

	@Transactional
	public void reject(Long signupId) {
		if (!pendingSignupRepository.existsById(signupId)) {
			throw new NotFoundException("가입 신청을 찾을 수 없어요: " + signupId);
		}
		pendingSignupRepository.deleteById(signupId);
	}

	@Transactional
	public MemberDto changeRole(Long memberId, Role role) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new NotFoundException("회원을 찾을 수 없어요: " + memberId));
		member.setRole(role);
		return MemberDto.from(member);
	}

	@Transactional
	public void delete(Long memberId) {
		if (!memberRepository.existsById(memberId)) {
			throw new NotFoundException("회원을 찾을 수 없어요: " + memberId);
		}
		memberRepository.deleteById(memberId);
	}
}
