// 관리자 "/admin/courses" mock 데이터 — 기수/과정/반 개설, 반별 총원, 담당 강사 배정,
// 반별 제출 현황(카운트만)을 관리한다. admin-course-management-scope 메모리 원칙대로
// 여기 제출 현황은 이름 없이 숫자만 — 콘텐츠 분석 화면(대시보드/키워드/갤러리)과는
// 완전히 분리된 화면이다. 반 이름·제출 현황 숫자는 mockAdminOverview.js와 맞춰뒀다.
//
// 강사 배정 규칙(사용자가 정정):
// - 전임교수는 "과목" 단위로 한 명 — 같은 과목의 모든 반에 동일하게 적용된다(반마다 다르지 않음).
// - 실습교수는 "반" 단위로 배정된다.
// - 수업 방식이 "줌"이면 전임교수가 화상으로 여러 반을 겸하고, 반마다 다른 실습교수가 실습을
//   담당한다(전임 ≠ 실습 가능). "직강"이면 전임교수가 그 반을 직접 대면으로 가르치므로
//   실습교수도 본인과 같다(전임 = 실습, 별도 배정 없음) — classes[].format으로 표현하고,
//   'direct'인 반은 labInstructorName을 따로 저장하지 않고 항상 과목의 leadInstructorName을
//   그대로 쓴다(labInstructorFor 헬퍼 참고).

import { mockMembers } from './mockAdminMembers'

export const instructorOptions = mockMembers.filter((m) => m.role === 'instructor').map((m) => m.name)

export const mockCourseManagement = [
  {
    id: 'vue',
    name: 'Vue.js 실전 프로젝트',
    leadInstructorName: '정민지',
    classes: [
      { id: 'vue-a', name: 'A반', capacity: 23, format: 'zoom', labInstructorName: '최유준', startDate: '2026-08-24', endDate: '2026-09-17', submission: { submitted: 19, total: 23 } },
      { id: 'vue-b', name: 'B반', capacity: 22, format: 'direct', labInstructorName: null, startDate: '2026-08-24', endDate: '2026-09-18', submission: { submitted: 20, total: 22 } },
    ],
  },
  {
    id: 'db',
    name: '데이터베이스 설계',
    leadInstructorName: '최유준',
    classes: [
      { id: 'db-a', name: 'A반', capacity: 20, format: 'zoom', labInstructorName: '정민지', startDate: '2026-08-31', endDate: '2026-09-10', submission: { submitted: 18, total: 20 } },
      { id: 'db-b', name: 'B반', capacity: 24, format: 'direct', labInstructorName: null, startDate: '2026-08-31', endDate: '2026-09-11', submission: { submitted: 21, total: 24 } },
    ],
  },
  {
    id: 'algo',
    name: '알고리즘 기초',
    leadInstructorName: '최유준',
    classes: [
      { id: 'algo-a', name: 'A반', capacity: 19, format: 'zoom', labInstructorName: '정민지', startDate: '2026-09-07', endDate: '2026-09-25', submission: { submitted: 17, total: 19 } },
      { id: 'algo-b', name: 'B반', capacity: 6, format: 'direct', labInstructorName: null, startDate: '2026-09-07', endDate: '2026-09-26', submission: { submitted: 4, total: 6 } },
    ],
  },
]

// 화면에 보여줄 실습교수 이름 — 직강이면 항상 전임교수와 동일.
export function labInstructorFor(course, cls) {
  return cls.format === 'direct' ? course.leadInstructorName : cls.labInstructorName
}
