// 관리자 "/admin/courses" mock 데이터 — 기수/과정/반 개설, 반별 총원, 담당 강사 배정,
// 반별 제출 현황(카운트만)을 관리한다. admin-course-management-scope 메모리 원칙대로
// 여기 제출 현황은 이름 없이 숫자만 — 콘텐츠 분석 화면(대시보드/키워드/갤러리)과는
// 완전히 분리된 화면이다. 반 이름·제출 현황 숫자는 mockAdminOverview.js와 맞춰뒀다.
//
// 반 하나에 강사가 2명 붙는다 — 전임교수(이론 강의 총괄)와 실습교수(실습 세션 담당).

import { mockMembers } from './mockAdminMembers'

export const instructorOptions = mockMembers.filter((m) => m.role === 'instructor').map((m) => m.name)

export const mockCourseManagement = [
  {
    id: 'vue',
    name: 'Vue.js 실전 프로젝트',
    classes: [
      {
        id: 'vue-a',
        name: 'A반',
        capacity: 23,
        leadInstructorName: '정민지',
        labInstructorName: '최유준',
        submission: { submitted: 19, total: 23 },
      },
      {
        id: 'vue-b',
        name: 'B반',
        capacity: 22,
        leadInstructorName: '최유준',
        labInstructorName: '정민지',
        submission: { submitted: 20, total: 22 },
      },
    ],
  },
  {
    id: 'db',
    name: '데이터베이스 설계',
    classes: [
      {
        id: 'db-a',
        name: 'A반',
        capacity: 20,
        leadInstructorName: '정민지',
        labInstructorName: '최유준',
        submission: { submitted: 18, total: 20 },
      },
      {
        id: 'db-b',
        name: 'B반',
        capacity: 24,
        leadInstructorName: '최유준',
        labInstructorName: '정민지',
        submission: { submitted: 21, total: 24 },
      },
    ],
  },
  {
    id: 'algo',
    name: '알고리즘 기초',
    classes: [
      {
        id: 'algo-a',
        name: 'A반',
        capacity: 19,
        leadInstructorName: '최유준',
        labInstructorName: '정민지',
        submission: { submitted: 17, total: 19 },
      },
      {
        id: 'algo-b',
        name: 'B반',
        capacity: 6,
        leadInstructorName: '정민지',
        labInstructorName: '최유준',
        submission: { submitted: 4, total: 6 },
      },
    ],
  },
]
