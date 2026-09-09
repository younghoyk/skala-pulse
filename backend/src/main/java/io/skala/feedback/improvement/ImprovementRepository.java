package io.skala.feedback.improvement;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImprovementRepository extends JpaRepository<Improvement, Long> {

	@EntityGraph(attributePaths = "course")
	List<Improvement> findAll();
}
