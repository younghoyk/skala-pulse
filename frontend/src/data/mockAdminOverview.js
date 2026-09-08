// 관리자 "/admin/dashboard" mock 데이터. Vue.js A반 항목은 mockInstructorData.js의
// score(이번 기수 3.2, 직전 기수 3.9)·rankItems 최하위(실습 시간)와 숫자를 일치시켜,
// 강사 화면과 관리자 화면이 같은 반에 대해 같은 이야기를 하도록 맞췄다. "하이라이트"는
// 하락폭 기준 정렬로만 만들고, 어떤 반이 왜 하락했는지는 계산하거나 주장하지 않는다.
//
// 대시보드는 2단계 구조다: 바깥은 강의(과목) 목록 + 합산 제출 현황만, 안쪽(과목 상세)은
// 강사 대시보드와 같은 레이아웃(막대그래프+순위 카드)으로 보여주되, 막대는 반마다 +
// 과목 전체 평균 + 다른 과목들 평균(벤치마크)이고, 순위 카드는 과목 전체(반 평균의 평균)다.
// 그래서 각 반에 rankItems를 갖고 있어야 과목 단위로 합칠 수 있다.

export const mockAdminClasses = [
  {
    id: 'vue-a',
    courseId: 'vue',
    courseName: 'Vue.js 실전 프로젝트',
    className: 'A반',
    currentAvg: 3.2,
    previousAvg: 3.9,
    respondentCount: 19,
    lowSampleThreshold: 5,
    worstItemLabel: '실습 시간',
    submission: { submitted: 19, total: 23 },
    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 2.0, previousAvgRank: 2.4 },
      { id: 'pace', label: '강의 속도', avgRank: 2.3, previousAvgRank: 2.6 },
      { id: 'practice', label: '실습 시간', avgRank: 4.5, previousAvgRank: 1.9 },
      { id: 'communication', label: '강사 소통', avgRank: 1.6, previousAvgRank: 2.0 },
      { id: 'material', label: '학습 자료', avgRank: 3.2, previousAvgRank: 3.6 },
    ],
  },
  {
    id: 'algo-b',
    courseId: 'algo',
    courseName: '알고리즘 기초',
    className: 'B반',
    currentAvg: 3.85,
    previousAvg: 3.6,
    respondentCount: 4,
    lowSampleThreshold: 5,
    worstItemLabel: '강의 속도',
    submission: { submitted: 4, total: 6 },
    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 1.8, previousAvgRank: 1.9 },
      { id: 'practice', label: '실습 시간', avgRank: 2.1, previousAvgRank: 2.0 },
      { id: 'material', label: '학습 자료', avgRank: 2.6, previousAvgRank: 2.5 },
      { id: 'pace', label: '강의 속도', avgRank: 3.5, previousAvgRank: 3.0 },
    ],
  },
  {
    id: 'db-a',
    courseId: 'db',
    courseName: '데이터베이스 설계',
    className: 'A반',
    currentAvg: 3.7,
    previousAvg: 3.6,
    respondentCount: 18,
    lowSampleThreshold: 5,
    worstItemLabel: '실습 시간',
    submission: { submitted: 18, total: 20 },
    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 2.0, previousAvgRank: 2.1 },
      { id: 'pace', label: '강의 속도', avgRank: 2.3, previousAvgRank: 2.0 },
      { id: 'material', label: '학습 자료', avgRank: 3.0, previousAvgRank: 2.9 },
      { id: 'practice', label: '실습 시간', avgRank: 3.8, previousAvgRank: 3.5 },
    ],
  },
  {
    id: 'vue-b',
    courseId: 'vue',
    courseName: 'Vue.js 실전 프로젝트',
    className: 'B반',
    currentAvg: 4.0,
    previousAvg: 3.9,
    respondentCount: 20,
    lowSampleThreshold: 5,
    worstItemLabel: '학습 자료',
    submission: { submitted: 20, total: 22 },
    rankItems: [
      { id: 'pace', label: '강의 속도', avgRank: 1.8, previousAvgRank: 2.0 },
      { id: 'assignment', label: '과제 난이도', avgRank: 2.2, previousAvgRank: 2.1 },
      { id: 'communication', label: '강사 소통', avgRank: 2.5, previousAvgRank: 2.6 },
      { id: 'practice', label: '실습 시간', avgRank: 3.0, previousAvgRank: 2.8 },
      { id: 'material', label: '학습 자료', avgRank: 3.5, previousAvgRank: 3.0 },
    ],
  },
  {
    id: 'db-b',
    courseId: 'db',
    courseName: '데이터베이스 설계',
    className: 'B반',
    currentAvg: 3.9,
    previousAvg: 3.85,
    respondentCount: 21,
    lowSampleThreshold: 5,
    worstItemLabel: '과제 난이도',
    submission: { submitted: 21, total: 24 },
    rankItems: [
      { id: 'pace', label: '강의 속도', avgRank: 1.9, previousAvgRank: 1.8 },
      { id: 'practice', label: '실습 시간', avgRank: 2.2, previousAvgRank: 2.0 },
      { id: 'material', label: '학습 자료', avgRank: 2.6, previousAvgRank: 2.7 },
      { id: 'assignment', label: '과제 난이도', avgRank: 3.4, previousAvgRank: 3.0 },
    ],
  },
  {
    id: 'algo-a',
    courseId: 'algo',
    courseName: '알고리즘 기초',
    className: 'A반',
    currentAvg: 4.2,
    previousAvg: 4.15,
    respondentCount: 17,
    lowSampleThreshold: 5,
    worstItemLabel: '학습 자료',
    submission: { submitted: 17, total: 19 },
    rankItems: [
      { id: 'assignment', label: '과제 난이도', avgRank: 1.8, previousAvgRank: 2.0 },
      { id: 'pace', label: '강의 속도', avgRank: 2.1, previousAvgRank: 1.9 },
      { id: 'practice', label: '실습 시간', avgRank: 2.4, previousAvgRank: 2.3 },
      { id: 'material', label: '학습 자료', avgRank: 3.7, previousAvgRank: 3.8 },
    ],
  },
]

// 바깥 대시보드용 — 과목별로 묶어서 반 개수 + 합산 제출 현황만 계산한다.
export function groupClassesByCourse() {
  const byId = new Map()
  for (const c of mockAdminClasses) {
    if (!byId.has(c.courseId)) {
      byId.set(c.courseId, {
        courseId: c.courseId,
        courseName: c.courseName,
        classes: [],
        submission: { submitted: 0, total: 0 },
      })
    }
    const group = byId.get(c.courseId)
    group.classes.push(c)
    group.submission.submitted += c.submission.submitted
    group.submission.total += c.submission.total
  }
  return [...byId.values()]
}

// 과목 상세 순위 카드용 — 그 과목에 속한 반들의 항목별 avgRank/previousAvgRank를 평균 낸다.
// (개별 반이 아니라 "과목 전체" 하나의 순위표를 만들기 위함.)
export function aggregateRankItems(classes) {
  const byItemId = new Map()
  for (const cls of classes) {
    for (const item of cls.rankItems) {
      if (!byItemId.has(item.id)) {
        byItemId.set(item.id, { id: item.id, label: item.label, avgRankSum: 0, previousAvgRankSum: 0, count: 0 })
      }
      const agg = byItemId.get(item.id)
      agg.avgRankSum += item.avgRank
      agg.previousAvgRankSum += item.previousAvgRank
      agg.count += 1
    }
  }
  return [...byItemId.values()].map((agg) => ({
    id: agg.id,
    label: agg.label,
    avgRank: agg.avgRankSum / agg.count,
    previousAvgRank: agg.previousAvgRankSum / agg.count,
  }))
}
