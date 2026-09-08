// 강사 "/instructor/feedbacks" mock 데이터. survey-branching-logic 메모리 표에 따르면
// 퀵태그·서술형 블록은 원인진단(순위 트렌드)이 아니라 Word2Vec 키워드 분석에만 쓰인다 —
// 그래서 여기 데이터는 대시보드의 rankItems와 별개로, 학생이 남긴 태그·한줄요약 텍스트만
// 재료로 삼는다. "실습" 관련 언급이 두드러지는 쪽으로 짜서 대시보드 내용과 자연스럽게
// 이어지지만, 화면 어디에도 "그래서 순위가 떨어졌다" 같은 인과 문구는 넣지 않는다.

export const mockKeywordGraph = {
  nodes: [
    { id: 'practice', label: '실습', weight: 18 },
    { id: 'time-short', label: '시간 부족', weight: 14 },
    { id: 'more-practice', label: '더 해보고 싶어요', weight: 7 },
    { id: 'assignment', label: '과제', weight: 9 },
    { id: 'pace', label: '진도', weight: 7 },
    { id: 'communication', label: '소통', weight: 6 },
    { id: 'good', label: '좋아요', weight: 8 },
    { id: 'material', label: '자료', weight: 5 },
  ],
  edges: [
    { id: 'e1', source: 'practice', target: 'time-short', weight: 12 },
    { id: 'e2', source: 'practice', target: 'more-practice', weight: 7 },
    { id: 'e3', source: 'assignment', target: 'pace', weight: 4 },
    { id: 'e4', source: 'communication', target: 'good', weight: 6 },
    { id: 'e5', source: 'pace', target: 'time-short', weight: 3 },
    { id: 'e6', source: 'practice', target: 'assignment', weight: 2 },
    { id: 'e7', source: 'material', target: 'more-practice', weight: 2 },
  ],
}

// 위치는 노드 개수가 적어(8개) 수동으로 배치 — 별도 자동 레이아웃 라이브러리는 쓰지 않는다.
export const keywordNodePositions = {
  practice: { x: 320, y: 160 },
  'time-short': { x: 500, y: 100 },
  'more-practice': { x: 400, y: 280 },
  assignment: { x: 100, y: 80 },
  pace: { x: 140, y: 240 },
  communication: { x: 580, y: 260 },
  good: { x: 640, y: 340 },
  material: { x: 40, y: 340 },
}

// 익명 — 이름/학생ID 없이 태그와 한 줄 요약만. QuickTagBlock에서 학생이 실제 남길 법한 형태.
export const mockFeedbackGallery = [
  { id: 'f1', tags: ['실습 부족'], summary: '직접 해볼 시간이 너무 부족했어요' },
  { id: 'f2', tags: ['실습 부족', '과제 많음'], summary: '실습은 부족한데 과제는 많아서 벅찼어요' },
  { id: 'f3', tags: ['소통 많음'], summary: '질문에 바로바로 답해주셔서 좋았어요' },
  { id: 'f4', tags: ['실습 부족'], summary: '이론만 계속 들으니까 지루했어요' },
  { id: 'f5', tags: ['속도 빠름', '실습 부족'], summary: '진도도 빠른데 실습할 시간까지 없었어요' },
  { id: 'f6', tags: ['자료 부족'], summary: '참고할 자료가 더 있으면 좋겠어요' },
]
