package io.skala.feedback.improvement;

import io.skala.feedback.course.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 "개선 과제" 체크리스트 항목 — admin-improvements-not-kanban 메모리 원칙대로
// 진행전/진행중/완료 3단 상태 컬럼이 없다. 완료 처리는 그냥 이 행을 지우는 것.
@Entity
@Table(name = "improvement")
@Getter
@Setter
@NoArgsConstructor
public class Improvement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(nullable = false)
	private String title;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	public Improvement(Course course, String title) {
		this.course = course;
		this.title = title;
		this.createdAt = Instant.now();
	}
}
