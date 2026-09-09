package io.skala.feedback.course;

import java.time.LocalDate;

public record ClassDto(
		Long id,
		String name,
		Integer capacity,
		ClassFormat format,
		String labInstructorName,
		LocalDate startDate,
		LocalDate endDate,
		SubmissionDto submission) {

	// 직강 반은 실습교수 = 전임교수 — labInstructorFor() 규칙을 서비스가 아니라 DTO 변환
	// 시점에 적용해서, 컨트롤러/서비스 쪽엔 항상 "화면에 보여줄 이름"만 흘러가게 한다.
	static ClassDto from(ClassGroup classGroup, String leadInstructorName) {
		String labName = classGroup.getFormat() == ClassFormat.DIRECT
				? leadInstructorName
				: (classGroup.getLabInstructor() != null ? classGroup.getLabInstructor().getName() : null);
		return new ClassDto(
				classGroup.getId(),
				classGroup.getName(),
				classGroup.getCapacity(),
				classGroup.getFormat(),
				labName,
				classGroup.getStartDate(),
				classGroup.getEndDate(),
				new SubmissionDto(classGroup.getSubmittedCount(), classGroup.getTotalCount()));
	}
}
