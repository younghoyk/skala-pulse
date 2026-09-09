package io.skala.feedback.survey;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class SurveyTemplateController {

	private final SurveyTemplateService surveyTemplateService;

	public SurveyTemplateController(SurveyTemplateService surveyTemplateService) {
		this.surveyTemplateService = surveyTemplateService;
	}

	@GetMapping("/api/courses/{courseId}/survey-template")
	public SurveyTemplateGraphDto getGraph(@PathVariable Long courseId) {
		return surveyTemplateService.getGraph(courseId);
	}

	@PutMapping("/api/courses/{courseId}/survey-template")
	public void saveGraph(@PathVariable Long courseId, @RequestBody SurveyTemplateGraphDto graph) {
		surveyTemplateService.saveGraph(courseId, graph);
	}
}
