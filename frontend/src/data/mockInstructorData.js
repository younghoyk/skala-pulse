// 강사 대시보드 mock 데이터 — cause-diagnosis-logic 메모리의 원칙을 그대로 따른다:
// 개인 데이터 없음(반 평균만), 종합점수와 세부 항목 순위는 서로 다른 카드로 병렬 제공,
// 인과관계는 계산/주장하지 않는다. 화면은 이 데이터를 그대로 juxtapose 할 뿐이다.

export const mockInstructorClass = {
  courseName: 'Vue.js 실전 프로젝트',
  className: 'SKALA 3기 · A반',
  classShortLabel: 'A반', // 차트 범례처럼 좁은 공간에 쓸 짧은 이름 — 강사가 여러 반을 담당할 수 있어 "우리 반" 같은 모호한 표현 대신 구체적인 반 이름을 쓴다
  lowSampleThreshold: 5,

  // 종합점수 블록 — 주차별 반 평균 vs 전체 익명 벤치마크. 개인 점수는 없음.
  scoreTrend: [
    { week: '1주차', classAvg: 4.1, benchmarkAvg: 3.8, respondentCount: 22 },
    { week: '2주차', classAvg: 3.9, benchmarkAvg: 3.85, respondentCount: 24 },
    { week: '3주차', classAvg: 3.2, benchmarkAvg: 3.8, respondentCount: 23 },
  ],

  // 카드순위 블록 — 항목별 이번 주 반 평균 순위 vs 지난주 반 평균 순위.
  // 화면에는 avgRank 소수값을 노출하지 않고, 정렬된 1위~N위 순위표 + 순위 변동만 보여준다.
  rankItems: [
    { id: 'assignment', label: '과제 난이도', avgRank: 2.0, previousAvgRank: 2.4 },
    { id: 'pace', label: '강의 속도', avgRank: 2.3, previousAvgRank: 2.6 },
    { id: 'practice', label: '실습 시간', avgRank: 4.5, previousAvgRank: 1.9 },
    { id: 'communication', label: '강사 소통', avgRank: 1.6, previousAvgRank: 2.0 },
    { id: 'material', label: '학습 자료', avgRank: 3.2, previousAvgRank: 3.6 },
  ],

  submission: { submitted: 19, total: 23 },
}
