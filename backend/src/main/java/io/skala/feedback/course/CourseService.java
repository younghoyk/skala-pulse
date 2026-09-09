package io.skala.feedback.course;

import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.member.Member;
import io.skala.feedback.member.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CourseService {

	private final CourseRepository courseRepository;
	private final ClassGroupRepository classGroupRepository;
	private final MemberRepository memberRepository;

	public CourseService(
			CourseRepository courseRepository,
			ClassGroupRepository classGroupRepository,
			MemberRepository memberRepository) {
		this.courseRepository = courseRepository;
		this.classGroupRepository = classGroupRepository;
		this.memberRepository = memberRepository;
	}

	public List<CourseDto> listCourses() {
		return courseRepository.findAll().stream().map(CourseDto::from).toList();
	}

	@Transactional
	public CourseDto createCourse(NewCourseRequest request) {
		Member lead = findMember(request.leadInstructorId());
		Course course = new Course(request.name(), lead);
		return CourseDto.from(courseRepository.save(course));
	}

	@Transactional
	public void deleteCourse(Long courseId) {
		if (!courseRepository.existsById(courseId)) {
			throw new NotFoundException("과정을 찾을 수 없어요: " + courseId);
		}
		courseRepository.deleteById(courseId);
	}

	@Transactional
	public CourseDto addClass(Long courseId, NewClassRequest request) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("과정을 찾을 수 없어요: " + courseId));
		Member labInstructor = request.format() == ClassFormat.ZOOM && request.labInstructorId() != null
				? findMember(request.labInstructorId())
				: null;
		ClassGroup classGroup = new ClassGroup(
				course, request.name(), request.capacity(), request.format(), labInstructor,
				request.startDate(), request.endDate());
		course.getClasses().add(classGroup);
		return CourseDto.from(courseRepository.save(course));
	}

	@Transactional
	public ClassDto updateClass(Long classId, ClassUpdateRequest request) {
		ClassGroup classGroup = classGroupRepository.findById(classId)
				.orElseThrow(() -> new NotFoundException("반을 찾을 수 없어요: " + classId));
		if (request.capacity() != null) {
			classGroup.setCapacity(request.capacity());
		}
		if (request.format() != null) {
			classGroup.setFormat(request.format());
		}
		if (request.format() == ClassFormat.DIRECT) {
			classGroup.setLabInstructor(null);
		} else if (request.labInstructorId() != null) {
			classGroup.setLabInstructor(findMember(request.labInstructorId()));
		}
		if (request.startDate() != null) {
			classGroup.setStartDate(request.startDate());
		}
		if (request.endDate() != null) {
			classGroup.setEndDate(request.endDate());
		}
		String leadName = classGroup.getCourse().getLeadInstructor().getName();
		return ClassDto.from(classGroup, leadName);
	}

	@Transactional
	public void deleteClass(Long classId) {
		if (!classGroupRepository.existsById(classId)) {
			throw new NotFoundException("반을 찾을 수 없어요: " + classId);
		}
		classGroupRepository.deleteById(classId);
	}

	private Member findMember(Long id) {
		return memberRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("회원을 찾을 수 없어요: " + id));
	}
}
