package io.skala.feedback.survey;

import io.skala.feedback.survey.SurveyBlockDto.ItemDto;
import io.skala.feedback.survey.SurveyBlockDto.TagDto;
import java.util.List;

// 관리자 설문 빌더 백엔드가 아직 없어서(이후 Phase), 모든 반의 설문이 프론트
// mockSurveys.js에 있던 고정 템플릿 하나를 그대로 공유한다 — 과정마다 다른 문항을
// 만드는 건 이번 범위 밖.
final class SurveyBlockTemplate {

	private SurveyBlockTemplate() {
	}

	static List<SurveyBlockDto> blocks() {
		return List.of(
				new SurveyBlockDto(
						"rank",
						"card-rank",
						"아래 항목을 만족스러웠던 순서대로 배치해주세요",
						List.of(
								new ItemDto("assignment", "과제 난이도"),
								new ItemDto("pace", "강의 속도"),
								new ItemDto("practice", "실습 시간"),
								new ItemDto("communication", "강사 소통"),
								new ItemDto("material", "학습 자료")),
						null, null, null, null),
				new SurveyBlockDto(
						"tag",
						"quick-tag",
						"이번 주 가장 크게 느낀 점을 골라주세요",
						null,
						List.of(
								new TagDto("assignment-more", "과제 많음", "과제 양이 너무 많았어요"),
								new TagDto("assignment-less", "과제 부족", "과제가 너무 적어서 아쉬웠어요"),
								new TagDto("pace-fast", "속도 빠름", "진도가 너무 빨라서 따라가기 힘들었어요"),
								new TagDto("pace-slow", "속도 느림", "진도가 느려서 지루했어요"),
								new TagDto("practice-more", "실습 많음", "실습 시간이 너무 길었어요"),
								new TagDto("practice-less", "실습 부족", "직접 해볼 시간이 부족했어요"),
								new TagDto("communication-more", "소통 많음", "질문·소통 시간이 충분해서 좋았어요"),
								new TagDto("communication-less", "소통 부족", "질문할 기회가 부족했어요"),
								new TagDto("material-more", "자료 많음", "자료가 너무 많아서 다 보기 부담스러웠어요"),
								new TagDto("material-less", "자료 부족", "참고할 자료가 더 있으면 좋겠어요")),
						100, null, null),
				new SurveyBlockDto(
						"score-lab", "score-drag", "실습교수님은 어떠셨나요?", null, null, null, 0, 5),
				new SurveyBlockDto(
						"score", "score-drag", "이번 수업 종합 만족도를 평가해주세요", null, null, null, 0, 5));
	}
}
