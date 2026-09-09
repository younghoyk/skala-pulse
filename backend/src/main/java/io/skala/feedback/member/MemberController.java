package io.skala.feedback.member;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@PreAuthorize("hasRole('ADMIN')")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping
	public List<MemberDto> listMembers() {
		return memberService.listMembers();
	}

	@GetMapping("/pending")
	public List<PendingSignupDto> listPending() {
		return memberService.listPending();
	}

	// 가입 신청 자체는 아직 계정이 없는 사람이 하는 행동이라 인증 없이 열어둔다
	// (SecurityConfig에서 POST /api/members/signup을 permitAll 처리).
	@PostMapping("/signup")
	@PreAuthorize("permitAll()")
	@ResponseStatus(HttpStatus.CREATED)
	public PendingSignupDto signup(@Valid @RequestBody SignupRequest request) {
		return memberService.requestSignup(request);
	}

	@PostMapping("/{signupId}/approve")
	public MemberDto approve(@PathVariable Long signupId) {
		return memberService.approve(signupId);
	}

	@DeleteMapping("/{signupId}/reject")
	public void reject(@PathVariable Long signupId) {
		memberService.reject(signupId);
	}

	@PatchMapping("/{memberId}")
	public MemberDto changeRole(@PathVariable Long memberId, @Valid @RequestBody RoleChangeRequest request) {
		return memberService.changeRole(memberId, request.role());
	}

	@DeleteMapping("/{memberId}")
	public void delete(@PathVariable Long memberId) {
		memberService.delete(memberId);
	}
}
