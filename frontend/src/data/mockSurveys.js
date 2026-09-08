// 이후 관리자 노드 빌더 / 백엔드가 생성할 설문 스키마를 미리 흉내낸 mock 데이터.
// blocks 배열의 순서가 곧 응답 흐름이다. 각 block.type 은 survey-branching-logic
// 메모리에서 정의한 고정 팔레트(카드순위/점수드래그/단답형/서술형/퀵태그) 중 하나.

// 퀵태그는 카드순위 항목(과제/속도/실습/소통/자료)마다 "많음/부족" 양방향으로 짝을 이룬다 —
// 방향이 있어야 원인 진단에서 어느 쪽으로 문제가 있는지 구분할 수 있다.
const TAG_LIBRARY = {
  assignment: {
    more: { id: 'assignment-more', label: '과제 많음', placeholderHint: '과제 양이 너무 많았어요' },
    less: { id: 'assignment-less', label: '과제 부족', placeholderHint: '과제가 너무 적어서 아쉬웠어요' },
  },
  pace: {
    more: { id: 'pace-fast', label: '속도 빠름', placeholderHint: '진도가 너무 빨라서 따라가기 힘들었어요' },
    less: { id: 'pace-slow', label: '속도 느림', placeholderHint: '진도가 느려서 지루했어요' },
  },
  practice: {
    more: { id: 'practice-more', label: '실습 많음', placeholderHint: '실습 시간이 너무 길었어요' },
    less: { id: 'practice-less', label: '실습 부족', placeholderHint: '직접 해볼 시간이 부족했어요' },
  },
  communication: {
    more: { id: 'communication-more', label: '소통 많음', placeholderHint: '질문·소통 시간이 충분해서 좋았어요' },
    less: { id: 'communication-less', label: '소통 부족', placeholderHint: '질문할 기회가 부족했어요' },
  },
  material: {
    more: { id: 'material-more', label: '자료 많음', placeholderHint: '자료가 너무 많아서 다 보기 부담스러웠어요' },
    less: { id: 'material-less', label: '자료 부족', placeholderHint: '참고할 자료가 더 있으면 좋겠어요' },
  },
}

function tagsFor(dimensions) {
  return dimensions.flatMap((dim) => [TAG_LIBRARY[dim].more, TAG_LIBRARY[dim].less])
}

export const mockSurveys = [
  {
    id: 'survey-vue-w1',
    courseName: 'Vue.js 실전 프로젝트',
    className: 'SKALA 3기 · A반',
    blocks: [
      {
        id: 'score',
        type: 'score-drag',
        question: '이번 수업 종합 만족도를 평가해주세요',
        min: 0,
        max: 5,
      },
      {
        id: 'rank',
        type: 'card-rank',
        question: '아래 항목을 만족스러웠던 순서대로 배치해주세요',
        items: [
          { id: 'assignment', label: '과제 난이도' },
          { id: 'pace', label: '강의 속도' },
          { id: 'practice', label: '실습 시간' },
          { id: 'communication', label: '강사 소통' },
          { id: 'material', label: '학습 자료' },
        ],
      },
      {
        id: 'tag',
        type: 'quick-tag',
        question: '이번 주 가장 크게 느낀 점을 골라주세요',
        tags: tagsFor(['assignment', 'pace', 'practice', 'communication', 'material']),
        summaryMaxLength: 100,
      },
    ],
  },
  {
    id: 'survey-db-w1',
    courseName: '데이터베이스 설계',
    className: 'SKALA 3기 · A반',
    blocks: [
      {
        id: 'score',
        type: 'score-drag',
        question: '이번 수업 종합 만족도를 평가해주세요',
        min: 0,
        max: 5,
      },
      {
        id: 'rank',
        type: 'card-rank',
        question: '아래 항목을 만족스러웠던 순서대로 배치해주세요',
        items: [
          { id: 'assignment', label: '과제 난이도' },
          { id: 'pace', label: '강의 속도' },
          { id: 'practice', label: '실습 시간' },
          { id: 'material', label: '학습 자료' },
        ],
      },
      {
        id: 'tag',
        type: 'quick-tag',
        question: '이번 주 가장 크게 느낀 점을 골라주세요',
        tags: tagsFor(['assignment', 'pace', 'practice', 'material']),
        summaryMaxLength: 100,
      },
    ],
  },
]

// 지난 기수 피드백이 실제로 반영된 사례 — /survey/complete 배너용 mock.
// 설문은 강의가 끝날 때 한 번만 진행되므로(주차별 반복 아님), "지난 주"가 아니라
// "지난 기수" 피드백이 이번 기수 강의에 반영됐다는 맥락이다.
export const mockPreviousFeedbackNotice =
  '지난 기수 "실습 시간 부족" 피드백을 반영해 이번 기수부터 실습 세션이 30분 늘어났어요.'
