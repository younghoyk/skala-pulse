package io.skala.feedback.course;

import io.skala.feedback.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 실습교수는 반(ClassGroup) 단위로 배정된다. 수업 방식이 direct(직강)면 전임교수가
// 실습도 겸해서 labInstructor는 null로 둔다 — 표시할 땐 course.leadInstructor를 그대로 쓴다
// (프론트 mockAdminCourseManagement.labInstructorFor와 동일한 규칙, 서비스 계층에서 재현).
@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
public class ClassGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ClassFormat format;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lab_instructor_id")
	private Member labInstructor;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "submitted_count", nullable = false)
	private Integer submittedCount = 0;

	// 총원 — 관리자가 입력하는 값이 아니라 CourseService.resyncClassHeadcounts()가 실제
	// 회원가입 인원(반 이름 기준)으로 채운다. 생성 직후 동기화 전까지의 안전한 초깃값 0.
	@Column(name = "total_count", nullable = false)
	private Integer totalCount = 0;

	public ClassGroup(Course course, String name, ClassFormat format, Member labInstructor,
			LocalDate startDate, LocalDate endDate) {
		this.course = course;
		this.name = name;
		this.format = format;
		this.labInstructor = labInstructor;
		this.startDate = startDate;
		this.endDate = endDate;
		this.submittedCount = 0;
	}
}
