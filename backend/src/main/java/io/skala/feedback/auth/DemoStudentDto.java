package io.skala.feedback.auth;

// 로그인 전 화면에서 "누구로 데모 로그인할지" 고르는 목록에 쓰는 최소 정보.
public record DemoStudentDto(Long id, String name, String className, String serialNumber) {
}
