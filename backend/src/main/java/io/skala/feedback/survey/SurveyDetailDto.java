package io.skala.feedback.survey;

import io.skala.feedback.survey.SurveyTemplateGraphDto.EdgeDto;
import java.time.LocalDate;
import java.util.List;

public record SurveyDetailDto(
		Long id,
		Long classId,
		String courseName,
		String className,
		LocalDate endDate,
		boolean submitted,
		List<SurveyBlockDto> blocks,
		List<EdgeDto> edges,
		String startBlockId,
		String endBlockId) {

	static SurveyDetailDto from(Survey survey, SurveyGraphResult graph) {
		var cls = survey.getClassGroup();
		boolean submitted = cls.getSubmittedCount() >= cls.getTotalCount();
		return new SurveyDetailDto(
				survey.getId(), cls.getId(), cls.getCourse().getName(), cls.getName(), cls.getEndDate(),
				submitted, graph.blocks(), graph.edges(), graph.startBlockId(), graph.endBlockId());
	}
}
