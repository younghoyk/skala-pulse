package io.skala.feedback.survey;

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

// 익명 응답 — 어떤 학생이 냈는지는 어디에도 저장하지 않는다(admin-course-management-scope
// 메모리의 "익명은 플랫폼 전체에 대해 성립" 원칙). answers는 블록id -> 답변 형태의 JSON을
// 그대로 문자열로 저장 — 지금 단계에선 집계/분석 없이 원문만 보존하면 된다.
@Entity
@Table(name = "survey_response")
@Getter
@Setter
@NoArgsConstructor
public class SurveyResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_id", nullable = false)
	private Survey survey;

	@Column(name = "submitted_at", nullable = false)
	private Instant submittedAt;

	@Column(name = "answers", nullable = false, columnDefinition = "TEXT")
	private String answersJson;

	public SurveyResponse(Survey survey, String answersJson) {
		this.survey = survey;
		this.answersJson = answersJson;
		this.submittedAt = Instant.now();
	}
}
