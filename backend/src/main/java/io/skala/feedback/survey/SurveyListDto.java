package io.skala.feedback.survey;

import java.time.LocalDate;

public record SurveyListDto(
		Long id,
		Long classId,
		String courseName,
		String className,
		LocalDate endDate,
		boolean submitted) {

	static SurveyListDto from(Survey survey) {
		var cls = survey.getClassGroup();
		boolean submitted = cls.getSubmittedCount() >= cls.getTotalCount();
		return new SurveyListDto(
				survey.getId(), cls.getId(), cls.getCourse().getName(), cls.getName(), cls.getEndDate(), submitted);
	}
}
