package io.skala.feedback.course;

import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.member.Member;
import io.skala.feedback.member.MemberRepository;
import java.util.List;
import java.util.Objects;
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

	// 이 회원이 전임(과목 전체 공통) 또는 실습(반 단위, 줌 수업만)으로 배정된 반만 골라낸다 —
	// 직강 반은 labInstructor가 없어서(전임이 실습도 겸함) LEAD 조건에서만 걸린다.
	public List<MyCourseDto> myCourses(Long memberId) {
		return classGroupRepository.findAll().stream()
				.map(cls -> {
					boolean isLead = cls.getCourse().getLeadInstructor().getId().equals(memberId);
					boolean isLab = cls.getFormat() == ClassFormat.ZOOM
							&& cls.getLabInstructor() != null
							&& cls.getLabInstructor().getId().equals(memberId);
					if (!isLead && !isLab) return null;
					String myRole = isLead ? "LEAD" : "LAB";
					return new MyCourseDto(
							cls.getId(), cls.getCourse().getName(), cls.getName(), myRole,
							new SubmissionDto(cls.getSubmittedCount(), cls.getTotalCount()));
				})
				.filter(Objects::nonNull)
				.toList();
	}

	@Transactional
	public CourseDto createCourse(NewCourseRequest request) {
		Member lead = findMember(request.leadInstructorId());
		Course course = new Course(request.name(), lead);
		return CourseDto.from(courseRepository.save(course));
	}

	@Transactional
	public CourseDto updateCourse(Long courseId, CourseUpdateRequest request) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("과정을 찾을 수 없어요: " + courseId));
		course.setLeadInstructor(findMember(request.leadInstructorId()));
		return CourseDto.from(course);
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
		return ClassDto.from(classGroup, classGroup.getCourse().getLeadInstructor());
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
