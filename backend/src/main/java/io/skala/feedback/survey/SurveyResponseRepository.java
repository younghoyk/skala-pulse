package io.skala.feedback.survey;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

	List<SurveyResponse> findBySurveyIdIn(List<Long> surveyIds);
}
