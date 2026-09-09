package io.skala.feedback.auth;

import io.skala.feedback.member.Member;
import io.skala.feedback.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(JwtService jwtService, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.jwtService = jwtService;
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// 이메일이 없거나 비밀번호가 안 맞거나 둘 다 같은 메시지로 거절한다 — 어느 쪽이었는지
	// 알려주면 그 이메일로 가입돼있는지 여부가 새어나간다(계정 존재 여부 비노출).
	public TokenResponse login(LoginRequest request) {
		Member member = memberRepository.findByEmail(request.email()).orElse(null);
		if (member == null || !passwordEncoder.matches(request.password(), member.getPasswordHash())) {
			throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않아요.");
		}
		String token = jwtService.issueToken(member.getName(), member.getRole(), member.getClassName(), member.getId());
		return new TokenResponse(token, member.getRole(), member.getName(), member.getClassName());
	}
}
