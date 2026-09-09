package io.skala.feedback.analysis;

import io.skala.feedback.auth.AuthenticatedUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 강사는 본인 담당 여부와 무관하게 이 서비스의 다른 course 엔드포인트들처럼 courseId만
// 맞으면 조회 가능(기존 관례). admin은 memberId 없는 자유 로그인이라 myLabScore 계산이
// 항상 null로 안전하게 빠진다(AnalysisService의 Member.getId().equals(null) 처리).
@RestController
@PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
public class AnalysisController {

	private final AnalysisService analysisService;

	public AnalysisController(AnalysisService analysisService) {
		this.analysisService = analysisService;
	}

	@GetMapping("/api/courses/{courseId}/analysis")
	public CourseAnalysisDto analyzeCourse(@PathVariable Long courseId, Authentication authentication) {
		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
		return analysisService.analyzeCourse(courseId, user.memberId());
	}
}
