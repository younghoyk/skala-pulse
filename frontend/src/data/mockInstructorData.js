// 강사 대시보드 mock 데이터 — cause-diagnosis-logic 메모리의 원칙을 그대로 따른다:
// 개인 데이터 없음(반 평균만), 종합점수와 세부 항목 순위는 서로 다른 카드로 병렬 제공,
// 인과관계는 계산/주장하지 않는다. 화면은 이 데이터를 그대로 juxtapose 할 뿐이다.
//
// 강사 한 명이 여러 강의를 담당할 수 있어서 id로 조회하는 컬렉션으로 둔다.
// vue-a / algo-a 숫자는 mockAdminOverview.js의 같은 id 항목과 일치시켜뒀다 —
// 관리자 화면과 강사 화면이 같은 반에 대해 같은 이야기를 하도록.

export const mockInstructorCourses = {
  'vue-a': {
    id: 'vue-a',
    courseName: 'Vue.js 실전 프로젝트',
    className: 'SKALA 3기 · A반',
    classShortLabel: 'A반',
    lowSampleThreshold: 5,

    scoreTrend: [
      { week: '1주차', classAvg: 4.1, benchmarkAvg: 3.8, respondentCount: 22 },
      { week: '2주차', classAvg: 3.9, benchmarkAvg: 3.85, respondentCount: 24 },
      { week: '3주차', classAvg: 3.2, benchmarkAvg: 3.8, respondentCount: 23 },
    ],

    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 2.0, previousAvgRank: 2.4 },
      { id: 'pace', label: '강의 속도', avgRank: 2.3, previousAvgRank: 2.6 },
      { id: 'practice', label: '실습 시간', avgRank: 4.5, previousAvgRank: 1.9 },
      { id: 'communication', label: '강사 소통', avgRank: 1.6, previousAvgRank: 2.0 },
      { id: 'material', label: '학습 자료', avgRank: 3.2, previousAvgRank: 3.6 },
    ],

    submission: { submitted: 19, total: 23 },
  },

  'algo-a': {
    id: 'algo-a',
    courseName: '알고리즘 기초',
    className: 'SKALA 3기 · A반',
    classShortLabel: 'A반',
    lowSampleThreshold: 5,

    scoreTrend: [
      { week: '1주차', classAvg: 3.9, benchmarkAvg: 3.8, respondentCount: 16 },
      { week: '2주차', classAvg: 4.15, benchmarkAvg: 3.85, respondentCount: 18 },
      { week: '3주차', classAvg: 4.2, benchmarkAvg: 3.8, respondentCount: 17 },
    ],

    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 1.8, previousAvgRank: 2.0 },
      { id: 'pace', label: '강의 속도', avgRank: 2.1, previousAvgRank: 1.9 },
      { id: 'practice', label: '실습 시간', avgRank: 2.4, previousAvgRank: 2.3 },
      { id: 'material', label: '학습 자료', avgRank: 3.7, previousAvgRank: 3.8 },
    ],

    submission: { submitted: 17, total: 19 },
  },
}
