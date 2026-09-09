package io.skala.feedback.course;

import io.skala.feedback.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 전임교수는 과목(Course) 단위로 한 명 — 같은 과목의 모든 반에 동일하게 적용된다.
// admin-course-management-scope 메모리 원칙대로 실습교수는 ClassGroup 쪽에 반 단위로 붙는다.
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_instructor_id", nullable = false)
	private Member leadInstructor;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("id ASC")
	private List<ClassGroup> classes = new ArrayList<>();

	public Course(String name, Member leadInstructor) {
		this.name = name;
		this.leadInstructor = leadInstructor;
	}
}
