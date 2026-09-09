package io.skala.feedback.course;

import io.skala.feedback.member.Member;
import java.time.LocalDate;

public record ClassDto(
		Long id,
		String name,
		Integer capacity,
		ClassFormat format,
		Long labInstructorId,
		String labInstructorName,
		LocalDate startDate,
		LocalDate endDate,
		SubmissionDto submission) {

	// 직강 반은 실습교수 = 전임교수 — labInstructorFor() 규칙을 서비스가 아니라 DTO 변환
	// 시점에 적용해서, 컨트롤러/서비스 쪽엔 항상 "화면에 보여줄 이름/id"만 흘러가게 한다.
	static ClassDto from(ClassGroup classGroup, Member leadInstructor) {
		boolean direct = classGroup.getFormat() == ClassFormat.DIRECT;
		Member labMember = direct ? leadInstructor : classGroup.getLabInstructor();
		return new ClassDto(
				classGroup.getId(),
				classGroup.getName(),
				classGroup.getCapacity(),
				classGroup.getFormat(),
				labMember != null ? labMember.getId() : null,
				labMember != null ? labMember.getName() : null,
				classGroup.getStartDate(),
				classGroup.getEndDate(),
				new SubmissionDto(classGroup.getSubmittedCount(), classGroup.getTotalCount()));
	}
}
