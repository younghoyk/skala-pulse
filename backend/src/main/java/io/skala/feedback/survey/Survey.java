package io.skala.feedback.survey;

import io.skala.feedback.course.ClassGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 반(ClassGroup) 하나에 설문 하나 — 과정(커리큘럼 블록)이 끝날 때마다 그 반에 설문이
// 돈다는 원칙(student-survey-flow-refinements 메모리)대로, 반이 생기면 설문도 같이 생긴다.
@Entity
@Table(name = "survey")
@Getter
@Setter
@NoArgsConstructor
public class Survey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id", nullable = false, unique = true)
	private ClassGroup classGroup;

	public Survey(ClassGroup classGroup) {
		this.classGroup = classGroup;
	}
}
