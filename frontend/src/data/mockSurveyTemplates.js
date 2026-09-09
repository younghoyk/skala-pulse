// 관리자 설문 빌더 mock 데이터 — survey-builder-scope 메모리 원칙: 빈 캔버스가 아니라
// 항상 기본 시드 템플릿으로 시작한다. 순서는 카드순위 → 퀵태그 → 실습교수 점수드래그 →
// 종합평가 → 종료 — 세부 항목을 먼저 되짚어본 다음 마지막에 종합 판단을 내리도록
// 종합평가를 맨 뒤로 옮겼다(student-survey-flow-refinements 메모리 참고). 카드순위 항목을
// "전임/실습" 둘로 쪼개는 대신(항목이 너무 많아짐), 실습교수 몫은 점수드래그 블록을
// 하나 더 두는 쪽으로 정리했다 — 카드순위의 "강사 소통"은 전임교수 기준으로 남는다.
// 설문 구조는 강의(과목) 단위(courseId)로 결정되고, 반(class) 단위가 아니다.
//
// 노드 데이터 모양은 mockSurveys.js의 학생 설문 블록과 최대한 같게 맞췄다 — 나중에
// 빌더가 저장한 구조를 그대로 학생 설문 렌더링에 재사용할 수 있게.

function seedTemplate() {
  return {
    nodes: [
      {
        id: 'rank',
        type: 'card-rank',
        position: { x: 60, y: 160 },
        data: {
          question: '아래 항목을 만족스러웠던 순서대로 배치해주세요',
          items: [
            { id: 'assignment', label: '과제 난이도' },
            { id: 'pace', label: '강의 속도' },
            { id: 'practice', label: '실습 시간' },
            { id: 'communication', label: '강사 소통' },
            { id: 'material', label: '학습 자료' },
          ],
        },
      },
      {
        id: 'tag',
        type: 'quick-tag',
        position: { x: 340, y: 160 },
        data: {
          question: '이번 주 가장 크게 느낀 점을 골라주세요',
          tags: [
            { id: 'assignment-more', label: '과제 많음', placeholderHint: '과제 양이 너무 많았어요' },
            { id: 'pace-fast', label: '속도 빠름', placeholderHint: '진도가 너무 빨라서 따라가기 힘들었어요' },
            { id: 'practice-less', label: '실습 부족', placeholderHint: '직접 해볼 시간이 부족했어요' },
          ],
        },
      },
      {
        id: 'score-lab',
        type: 'score-drag',
        position: { x: 660, y: 160 },
        data: { question: '실습교수님은 어떠셨나요?' },
      },
      {
        id: 'score',
        type: 'score-drag',
        position: { x: 940, y: 160 },
        data: { question: '이번 수업 종합 만족도를 평가해주세요' },
      },
      {
        id: 'end',
        type: 'end',
        position: { x: 1220, y: 160 },
        data: {},
        deletable: false,
      },
    ],
    edges: [
      { id: 'e-rank-tag', source: 'rank', target: 'tag' },
      { id: 'e-tag-scorelab', source: 'tag', target: 'score-lab' },
      { id: 'e-scorelab-score', source: 'score-lab', target: 'score' },
      { id: 'e-score-end', source: 'score', target: 'end' },
    ],
  }
}

export const mockSurveyTemplatesByCourse = {
  vue: seedTemplate(),
  db: seedTemplate(),
  algo: seedTemplate(),
}

// 고정 블록 팔레트 — survey-branching-logic 메모리의 타입 목록과 동일.
export const blockPalette = [
  { type: 'score-drag', label: '점수드래그' },
  { type: 'card-rank', label: '카드순위' },
  { type: 'quick-tag', label: '퀵태그' },
  { type: 'short-answer', label: '단답형' },
  { type: 'long-answer', label: '서술형' },
]

export const blockTypeColors = {
  'score-drag': { border: 'var(--role-student)', soft: 'var(--role-student-soft)' },
  'card-rank': { border: 'var(--role-instructor)', soft: 'var(--role-instructor-soft)' },
  'quick-tag': { border: 'var(--role-admin)', soft: 'var(--role-admin-soft)' },
  'short-answer': { border: '#2563eb', soft: '#dbeafe' },
  'long-answer': { border: '#0d9488', soft: '#ccfbf1' },
  end: { border: '#6b7280', soft: '#e5e7eb' },
}

export const blockTypeLabels = {
  'score-drag': '점수드래그',
  'card-rank': '카드순위',
  'quick-tag': '퀵태그',
  'short-answer': '단답형',
  'long-answer': '서술형',
  end: '종료',
}
