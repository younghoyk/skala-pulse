package io.skala.feedback.course;

import java.util.List;

public record CourseDto(Long id, String name, String leadInstructorName, List<ClassDto> classes) {

	static CourseDto from(Course course) {
		String leadName = course.getLeadInstructor().getName();
		List<ClassDto> classes = course.getClasses().stream()
				.map(cls -> ClassDto.from(cls, leadName))
				.toList();
		return new CourseDto(course.getId(), course.getName(), leadName, classes);
	}
}
