// 관리자 "/admin/dashboard" mock 데이터. Vue.js A반 항목은 mockInstructorData.js의
// score(이번 기수 3.2, 직전 기수 3.9)·rankItems 최하위(실습 시간)와 숫자를 일치시켜,
// 강사 화면과 관리자 화면이 같은 반에 대해 같은 이야기를 하도록 맞췄다. "하이라이트"는
// 하락폭 기준 정렬로만 만들고, 어떤 반이 왜 하락했는지는 계산하거나 주장하지 않는다.
//
// 대시보드는 2단계 구조다: 바깥은 강의(과목) 목록 + 합산 제출 현황만, 안쪽에서 그 과목의
// 반들끼리 비교한다. courseId로 반을 과목 단위로 묶는다.

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
