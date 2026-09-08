// 관리자 키워드·피드백갤러리 mock 데이터 — 과목(courseId)에 종속된 화면이라, 키를
// mockAdminOverview.js의 courseId("vue"/"algo"/"db")와 통일한다. Vue.js·알고리즘 기초는
// mockInstructorFeedback.js의 vue-a/algo-a 내용을 그대로 재사용해서 강사 화면과 다른
// 이야기를 하지 않게 한다. 데이터베이스 설계는 새로 작성.

import { mockInstructorFeedbackByCourse } from './mockInstructorFeedback'

export const mockAdminFeedbackByCourse = {
  vue: mockInstructorFeedbackByCourse['vue-a'],
  algo: mockInstructorFeedbackByCourse['algo-a'],
  db: {
    keywordGraph: {
      nodes: [
        { id: 'assignment', label: '과제', weight: 14 },
        { id: 'difficult', label: '어려움', weight: 11 },
        { id: 'concept', label: '개념', weight: 8 },
        { id: 'practice', label: '실습', weight: 7 },
        { id: 'good', label: '좋아요', weight: 6 },
        { id: 'communication', label: '소통', weight: 5 },
      ],
      edges: [
        { id: 'e1', source: 'assignment', target: 'difficult', weight: 9 },
        { id: 'e2', source: 'assignment', target: 'concept', weight: 4 },
        { id: 'e3', source: 'practice', target: 'concept', weight: 3 },
        { id: 'e4', source: 'communication', target: 'good', weight: 4 },
        { id: 'e5', source: 'concept', target: 'good', weight: 2 },
      ],
    },
    nodePositions: {
      assignment: { x: 320, y: 140 },
      difficult: { x: 500, y: 100 },
      concept: { x: 400, y: 280 },
      practice: { x: 160, y: 320 },
      good: { x: 600, y: 320 },
      communication: { x: 620, y: 180 },
    },
    feedbackGallery: [
      { id: 'd1', tags: ['과제 많음'], summary: '과제가 생각보다 어려웠어요' },
      { id: 'd2', tags: ['과제 많음'], summary: '개념 이해가 안 된 상태로 과제를 풀려니 힘들었어요' },
      { id: 'd3', tags: ['소통 많음'], summary: '막힐 때마다 질문 받아주셔서 좋았어요' },
      { id: 'd4', tags: ['실습 부족'], summary: '실습 예제가 조금 더 있으면 좋겠어요' },
      { id: 'd5', tags: [], summary: '전체적으로 유익한 수업이었어요' },
    ],
  },
}
