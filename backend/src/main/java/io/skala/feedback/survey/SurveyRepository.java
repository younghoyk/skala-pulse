package io.skala.feedback.survey;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

	@EntityGraph(attributePaths = {"classGroup", "classGroup.course"})
	List<Survey> findAll();

	@EntityGraph(attributePaths = {"classGroup", "classGroup.course"})
	Optional<Survey> findById(Long id);

	List<Survey> findByClassGroupIdIn(List<Long> classGroupIds);
}
