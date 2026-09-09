package io.skala.feedback.auth;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 실제 Slack OAuth 붙기 전까지 프론트의 "원클릭 데모 로그인"을 대신한다. 교육생은 실제
// 승인된 회원 중 한 명으로 로그인(반 정보가 그 회원 걸 따라옴), 교수자/관리자는 특정
// 회원과 안 묶인 자유 입력 로그인 — AuthService.demoLogin 참고.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/demo-students")
	public List<DemoStudentDto> listDemoStudents() {
		return authService.listDemoStudents();
	}

	@PostMapping("/demo-login")
	public TokenResponse demoLogin(@Valid @RequestBody DemoLoginRequest request) {
		return authService.demoLogin(request);
	}
}
