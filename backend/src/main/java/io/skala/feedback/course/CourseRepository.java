package io.skala.feedback.course;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

	@EntityGraph(attributePaths = {"leadInstructor", "classes", "classes.labInstructor"})
	List<Course> findAll();
}
