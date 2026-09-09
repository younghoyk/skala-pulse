package io.skala.feedback.improvement;

public record ImprovementDto(Long id, Long courseId, String courseName, String title) {

	static ImprovementDto from(Improvement improvement) {
		return new ImprovementDto(
				improvement.getId(), improvement.getCourse().getId(), improvement.getCourse().getName(),
				improvement.getTitle());
	}
}
