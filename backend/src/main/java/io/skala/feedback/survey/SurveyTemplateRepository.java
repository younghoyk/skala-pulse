package io.skala.feedback.survey;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyTemplateRepository extends JpaRepository<SurveyTemplate, Long> {

	Optional<SurveyTemplate> findByCourseId(Long courseId);
}
