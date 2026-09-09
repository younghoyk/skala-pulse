package io.skala.feedback.course;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {

	@EntityGraph(attributePaths = {"course", "course.leadInstructor", "labInstructor"})
	List<ClassGroup> findAll();
}
