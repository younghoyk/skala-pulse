package io.skala.feedback.course;

// 강사 본인이 담당하는 반 목록(/instructor/courses)용 — myRole은 그 반에서의 역할이지
// 로그인 시점에 고정된 값이 아니다(같은 사람이 다른 반에선 다른 역할일 수 있음).
public record MyCourseDto(Long classId, String courseName, String className, String myRole, SubmissionDto submission) {
}
