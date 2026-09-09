package io.skala.feedback.survey;

import java.time.LocalDate;
import java.util.List;

public record SurveyDetailDto(
		Long id,
		Long classId,
		String courseName,
		String className,
		LocalDate endDate,
		boolean submitted,
		List<SurveyBlockDto> blocks) {

	static SurveyDetailDto from(Survey survey, List<SurveyBlockDto> blocks) {
		var cls = survey.getClassGroup();
		boolean submitted = cls.getSubmittedCount() >= cls.getTotalCount();
		return new SurveyDetailDto(
				survey.getId(), cls.getId(), cls.getCourse().getName(), cls.getName(), cls.getEndDate(),
				submitted, blocks);
	}
}
