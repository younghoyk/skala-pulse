package io.skala.feedback.course;

import io.skala.feedback.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class CourseController {

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping("/api/courses")
	public List<CourseDto> listCourses() {
		return courseService.listCourses();
	}

	@GetMapping("/api/courses/mine")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public List<MyCourseDto> myCourses(Authentication authentication) {
		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
		return courseService.myCourses(user.memberId());
	}

	@PostMapping("/api/courses")
	@ResponseStatus(HttpStatus.CREATED)
	public CourseDto createCourse(@Valid @RequestBody NewCourseRequest request) {
		return courseService.createCourse(request);
	}

	@PatchMapping("/api/courses/{courseId}")
	public CourseDto updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseUpdateRequest request) {
		return courseService.updateCourse(courseId, request);
	}

	@DeleteMapping("/api/courses/{courseId}")
	public void deleteCourse(@PathVariable Long courseId) {
		courseService.deleteCourse(courseId);
	}

	@PostMapping("/api/courses/{courseId}/classes")
	@ResponseStatus(HttpStatus.CREATED)
	public CourseDto addClass(@PathVariable Long courseId, @Valid @RequestBody NewClassRequest request) {
		return courseService.addClass(courseId, request);
	}

	@PatchMapping("/api/classes/{classId}")
	public ClassDto updateClass(@PathVariable Long classId, @RequestBody ClassUpdateRequest request) {
		return courseService.updateClass(classId, request);
	}

	@DeleteMapping("/api/classes/{classId}")
	public void deleteClass(@PathVariable Long classId) {
		courseService.deleteClass(classId);
	}
}
