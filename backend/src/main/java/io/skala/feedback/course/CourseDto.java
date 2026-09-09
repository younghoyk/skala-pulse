package io.skala.feedback.course;

import java.util.List;

public record CourseDto(Long id, String name, Long leadInstructorId, String leadInstructorName, List<ClassDto> classes) {

	static CourseDto from(Course course) {
		var lead = course.getLeadInstructor();
		List<ClassDto> classes = course.getClasses().stream()
				.map(cls -> ClassDto.from(cls, lead))
				.toList();
		return new CourseDto(course.getId(), course.getName(), lead.getId(), lead.getName(), classes);
	}
}
