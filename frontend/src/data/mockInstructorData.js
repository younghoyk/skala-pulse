// 강사 대시보드 mock 데이터 — cause-diagnosis-logic 메모리의 원칙을 그대로 따른다:
// 개인 데이터 없음(반 평균만), 종합점수와 세부 항목 순위는 서로 다른 카드로 병렬 제공,
// 인과관계는 계산/주장하지 않는다. 화면은 이 데이터를 그대로 juxtapose 할 뿐이다.
//
// 강사 한 명이 여러 강의를 담당할 수 있어서 id로 조회하는 컬렉션으로 둔다.
// vue-a / algo-a 숫자는 mockAdminOverview.js의 같은 id 항목과 일치시켜뒀다 —
// 관리자 화면과 강사 화면이 같은 반에 대해 같은 이야기를 하도록.
//
// 설문은 강의가 끝날 때 딱 한 번 진행되는 최종 만족도 조사다(주차별 반복 아님) —
// student-survey-flow-refinements 메모리 3번 참고. 그래서 종합점수는 여러 기수를
// 이어붙인 추이 그래프가 아니라, 이번 기수의 "반 평균 vs 전체 익명 벤치마크" 2개 막대만
// 보여준다. rankItems의 "지난 기간"은 같은 반(A반)의 직전 기수 결과를 뜻한다.
//
// score(종합점수)와 별개로 labScore(실습교수 점수)가 있다 — 전임교수는 score+rankItems로
// 만든 전체 종합 대시보드를, 실습교수는 labScore 하나만 보게 나뉜다(InstructorDashboardView
// 참고). 전임/실습은 로그인 시점이 아니라 **반마다 다른 역할**이라(mockAdminCourseManagement.js
// 의 leadInstructorName/labInstructorName 참고 — 같은 사람이 반마다 다르게 배정됨) 여기
// 반별로 `myRole`을 갖는다. 아래 데모 교수자는 정민지 기준 — vue-a에선 전임, algo-a에선 실습.

export const mockInstructorCourses = {
  'vue-a': {
    id: 'vue-a',
    courseName: 'Vue.js 실전 프로젝트',
    className: 'SKALA 3기 · A반',
    classShortLabel: 'A반',
    lowSampleThreshold: 5,
    myRole: 'lead',

    score: { classAvg: 3.2, benchmarkAvg: 3.8, respondentCount: 23 },
    labScore: { value: 2.8, respondentCount: 23 },

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
    myRole: 'lab',

    score: { classAvg: 4.2, benchmarkAvg: 3.8, respondentCount: 17 },
    labScore: { value: 4.0, respondentCount: 17 },

    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 1.8, previousAvgRank: 2.0 },
      { id: 'pace', label: '강의 속도', avgRank: 2.1, previousAvgRank: 1.9 },
      { id: 'practice', label: '실습 시간', avgRank: 2.4, previousAvgRank: 2.3 },
      { id: 'material', label: '학습 자료', avgRank: 3.7, previousAvgRank: 3.8 },
    ],

    submission: { submitted: 17, total: 19 },
  },
}
