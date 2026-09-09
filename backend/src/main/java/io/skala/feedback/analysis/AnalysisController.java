package io.skala.feedback.analysis;

import io.skala.feedback.auth.AuthenticatedUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('INSTRUCTOR')")
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
