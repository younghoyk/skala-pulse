package io.skala.feedback.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.course.ClassGroup;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final SurveyResponseRepository surveyResponseRepository;
	private final SurveyTemplateService surveyTemplateService;
	private final ObjectMapper objectMapper;

	public SurveyService(
			SurveyRepository surveyRepository,
			SurveyResponseRepository surveyResponseRepository,
			SurveyTemplateService surveyTemplateService,
			ObjectMapper objectMapper) {
		this.surveyRepository = surveyRepository;
		this.surveyResponseRepository = surveyResponseRepository;
		this.surveyTemplateService = surveyTemplateService;
		this.objectMapper = objectMapper;
	}

	public List<SurveyListDto> listAll() {
		return surveyRepository.findAll().stream().map(SurveyListDto::from).toList();
	}

	public SurveyDetailDto getDetail(Long surveyId) {
		Survey survey = findSurvey(surveyId);
		List<SurveyBlockDto> blocks = surveyTemplateService.blocksForCourse(courseId(survey));
		return SurveyDetailDto.from(survey, blocks);
	}

	@Transactional
	public SurveyDetailDto submitResponse(Long surveyId, SurveyResponseRequest request) {
		Survey survey = findSurvey(surveyId);
		String answersJson;
		try {
			answersJson = objectMapper.writeValueAsString(request.answers());
		} catch (Exception e) {
			throw new IllegalArgumentException("응답을 저장할 수 없어요.");
		}
		surveyResponseRepository.save(new SurveyResponse(survey, answersJson));

		ClassGroup classGroup = survey.getClassGroup();
		if (classGroup.getSubmittedCount() < classGroup.getTotalCount()) {
			classGroup.setSubmittedCount(classGroup.getSubmittedCount() + 1);
		}

		List<SurveyBlockDto> blocks = surveyTemplateService.blocksForCourse(courseId(survey));
		return SurveyDetailDto.from(survey, blocks);
	}

	private Survey findSurvey(Long id) {
		return surveyRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("설문을 찾을 수 없어요: " + id));
	}

	private Long courseId(Survey survey) {
		return survey.getClassGroup().getCourse().getId();
	}
}
