package io.skala.feedback.improvement;

import io.skala.feedback.common.NotFoundException;
import io.skala.feedback.course.Course;
import io.skala.feedback.course.CourseRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ImprovementService {

	private final ImprovementRepository improvementRepository;
	private final CourseRepository courseRepository;

	public ImprovementService(ImprovementRepository improvementRepository, CourseRepository courseRepository) {
		this.improvementRepository = improvementRepository;
		this.courseRepository = courseRepository;
	}

	// 화면이 과목명으로 그룹핑해서 보여주므로 과목명 기준으로 정렬해둔다.
	public List<ImprovementDto> listAll() {
		return improvementRepository.findAll().stream()
				.sorted(Comparator.comparing(i -> i.getCourse().getName()))
				.map(ImprovementDto::from)
				.toList();
	}

	@Transactional
	public ImprovementDto create(NewImprovementRequest request) {
		Course course = courseRepository.findById(request.courseId())
				.orElseThrow(() -> new NotFoundException("과정을 찾을 수 없어요: " + request.courseId()));
		Improvement saved = improvementRepository.save(new Improvement(course, request.title()));
		return ImprovementDto.from(saved);
	}

	@Transactional
	public void delete(Long id) {
		if (!improvementRepository.existsById(id)) {
			throw new NotFoundException("개선 과제를 찾을 수 없어요: " + id);
		}
		improvementRepository.deleteById(id);
	}
}
