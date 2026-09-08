// 관리자 "개선 과제" mock 데이터 — 예전엔 진행전/진행중/완료 3단 칸반이었는데,
// 완료는 "체크해서 지우는" 동작으로 바뀌었으므로 애초에 완료된 항목은 목록에 없다.
// 아직 남아있는 개선 과제만 과목별로 담아둔다.

export const mockPendingImprovements = [
  { id: 't1', title: '실습 예제 자료 추가', courseName: '데이터베이스 설계' },
  { id: 't2', title: '과제 난이도 조정 검토', courseName: '데이터베이스 설계' },
  { id: 't3', title: '진도 속도 조정', courseName: '알고리즘 기초' },
  { id: 'd1', title: '실습 세션 30분 연장 시범 적용', courseName: 'Vue.js 실전 프로젝트' },
  { id: 'd2', title: '학습 자료 목록 정리', courseName: 'Vue.js 실전 프로젝트' },
]
