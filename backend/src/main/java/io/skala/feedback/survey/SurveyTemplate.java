package io.skala.feedback.survey;

import io.skala.feedback.course.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 과정(course) 하나당 커스텀 설문 템플릿 최대 하나 — 없으면 SurveyBlockTemplate의
// 하드코딩 기본값을 그대로 쓴다(SurveyTemplateService.blocksForCourse). templateJson은
// 빌더 캔버스가 쓰는 {nodes,edges} 그래프 모양을 그대로 문자열로 저장한다.
@Entity
@Table(name = "survey_template")
@Getter
@Setter
@NoArgsConstructor
public class SurveyTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false, unique = true)
	private Course course;

	@Column(name = "template_json", nullable = false, columnDefinition = "TEXT")
	private String templateJson;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public SurveyTemplate(Course course, String templateJson) {
		this.course = course;
		this.templateJson = templateJson;
		this.updatedAt = Instant.now();
	}
}
