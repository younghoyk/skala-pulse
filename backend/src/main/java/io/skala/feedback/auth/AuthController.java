package io.skala.feedback.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 실제 Slack OAuth 붙기 전까지 프론트의 "원클릭 데모 로그인"을 그대로 받아 토큰을 발급한다 —
// 특정 회원 레코드와 묶이지 않는, 역할만 있는 세션이다(auth.loginAsDemo와 동일한 계약).
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final JwtService jwtService;

	public AuthController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@PostMapping("/demo-login")
	public TokenResponse demoLogin(@Valid @RequestBody DemoLoginRequest request) {
		String token = jwtService.issueToken(request.displayName(), request.role());
		return new TokenResponse(token, request.role(), request.displayName());
	}
}
