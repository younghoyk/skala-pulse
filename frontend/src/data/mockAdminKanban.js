// 관리자 "/admin/kanban" mock 데이터 — 여러 과목의 피드백에서 나온 개선 과제를
// 한 보드에서 같이 관리한다(칸반은 과목에 종속되지 않음). 카드 제목은 지금까지 쌓인
// mock 피드백 내용(실습 시간 부족, 학습 자료 부족, 과제 난이도 등)에서 자연스럽게
// 이어지는 과제로 짰다.

export const mockKanbanColumns = {
  todo: [
    { id: 't1', title: '실습 예제 자료 추가', courseName: '데이터베이스 설계' },
    { id: 't2', title: '과제 난이도 조정 검토', courseName: '데이터베이스 설계' },
    { id: 't3', title: '진도 속도 조정', courseName: '알고리즘 기초' },
  ],
  doing: [
    { id: 'd1', title: '실습 세션 30분 연장 시범 적용', courseName: 'Vue.js 실전 프로젝트' },
    { id: 'd2', title: '학습 자료 목록 정리', courseName: 'Vue.js 실전 프로젝트' },
  ],
  done: [
    { id: 'c1', title: '질문 게시판 개설', courseName: '알고리즘 기초' },
    { id: 'c2', title: '과제 가이드 문서 배포', courseName: '데이터베이스 설계' },
    { id: 'c3', title: '강의자료 PDF 제공', courseName: '알고리즘 기초' },
  ],
}
