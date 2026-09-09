package io.skala.feedback.course;

import java.util.List;

// 강사 본인이 담당하는 과정 목록(/instructor/courses)용 — 과정(courseId) 단위 하나가 한 항목.
// 전임교수(LEAD)는 그 과정의 반 전체가 classes에 담기고, 실습교수(LAB)는 본인이 배정된
// 반 하나만 담긴다(실습교수 배정은 원래 반 단위라서 — 과정 단위로 묶을 이유가 없음).
public record MyCourseDto(
		Long courseId, String courseName, String myRole, List<ClassRefDto> classes, SubmissionDto submission) {

	public record ClassRefDto(Long classId, String className) {
	}
}
