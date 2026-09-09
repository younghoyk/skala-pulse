package io.skala.feedback.course;

import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.course.MyCourseDto.ClassRefDto;
import io.skala.feedback.member.Member;
import io.skala.feedback.member.MemberRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	// 이 회원이 전임(과목 전체 공통)으로 이끄는 과정은 그 과정의 반 전체를 하나로 묶고,
	// 실습(반 단위, 줌 수업만)으로 배정된 반은 반 하나짜리 항목으로 — 실습교수 배정은
	// 원래 반 단위라서 과정 단위로 묶을 이유가 없다.
	public List<MyCourseDto> myCourses(Long memberId) {
		Map<Long, List<ClassGroup>> leadClassesByCourse = new LinkedHashMap<>();
		List<ClassGroup> labClasses = new ArrayList<>();

		for (ClassGroup cls : classGroupRepository.findAll()) {
			boolean isLead = cls.getCourse().getLeadInstructor().getId().equals(memberId);
			boolean isLab = cls.getFormat() == ClassFormat.ZOOM
					&& cls.getLabInstructor() != null
					&& cls.getLabInstructor().getId().equals(memberId);
			if (isLead) {
				leadClassesByCourse.computeIfAbsent(cls.getCourse().getId(), id -> new ArrayList<>()).add(cls);
			} else if (isLab) {
				labClasses.add(cls);
			}
		}

		List<MyCourseDto> result = new ArrayList<>();
		for (List<ClassGroup> classes : leadClassesByCourse.values()) {
			result.add(toMyCourseDto(classes, "LEAD"));
		}
		for (ClassGroup cls : labClasses) {
			result.add(toMyCourseDto(List.of(cls), "LAB"));
		}
		return result;
	}

	// classGroupRepository.findAll()은 정렬을 보장하지 않아서(반 id 순서가 뒤섞일 수 있음),
	// "1반~5반" 같은 범위 표시가 프론트에서 깨지지 않도록 여기서 id 순으로 정렬해둔다.
	private MyCourseDto toMyCourseDto(List<ClassGroup> unsortedClasses, String myRole) {
		List<ClassGroup> classes = unsortedClasses.stream()
				.sorted(Comparator.comparing(ClassGroup::getId))
				.toList();
		ClassGroup first = classes.get(0);
		int submitted = classes.stream().mapToInt(ClassGroup::getSubmittedCount).sum();
		int total = classes.stream().mapToInt(ClassGroup::getTotalCount).sum();
		List<ClassRefDto> classRefs = classes.stream()
				.map(c -> new ClassRefDto(c.getId(), c.getName()))
				.toList();
		return new MyCourseDto(
				first.getCourse().getId(), first.getCourse().getName(), myRole, classRefs,
				new SubmissionDto(submitted, total));
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
