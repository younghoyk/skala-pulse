package io.skala.feedback.survey;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/surveys")
@PreAuthorize("hasRole('STUDENT')")
public class SurveyController {

	private final SurveyService surveyService;

	public SurveyController(SurveyService surveyService) {
		this.surveyService = surveyService;
	}

	@GetMapping
	public List<SurveyListDto> listAll() {
		return surveyService.listAll();
	}

	@GetMapping("/{surveyId}")
	public SurveyDetailDto getDetail(@PathVariable Long surveyId) {
		return surveyService.getDetail(surveyId);
	}

	@PostMapping("/{surveyId}/responses")
	public SurveyDetailDto submitResponse(@PathVariable Long surveyId, @Valid @RequestBody SurveyResponseRequest request) {
		return surveyService.submitResponse(surveyId, request);
	}
}
