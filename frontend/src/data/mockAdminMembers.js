// 관리자 "/admin/members" mock 데이터 — 가입 승인 대기 목록과 이미 승인된 전체 회원 목록.

export const mockPendingSignups = [
  { id: 'p1', name: '김도윤', email: 'doyoon.kim@skala.io', requestedRole: 'student' },
  { id: 'p2', name: '이서연', email: 'seoyeon.lee@skala.io', requestedRole: 'student' },
  { id: 'p3', name: '박현우', email: 'hyunwoo.park@skala.io', requestedRole: 'instructor' },
]

export const mockMembers = [
  { id: 'm1', name: '데모 관리자', email: 'admin@skala.io', role: 'admin' },
  { id: 'm2', name: '정민지', email: 'minji.jung@skala.io', role: 'instructor' },
  { id: 'm3', name: '최유준', email: 'yujun.choi@skala.io', role: 'instructor' },
  { id: 'm4', name: '한소율', email: 'soyul.han@skala.io', role: 'student' },
  { id: 'm5', name: '오태양', email: 'taeyang.oh@skala.io', role: 'student' },
  { id: 'm6', name: '강나윤', email: 'nayoon.kang@skala.io', role: 'student' },
  { id: 'm7', name: '윤지호', email: 'jiho.yoon@skala.io', role: 'student' },
  { id: 'm8', name: '임서준', email: 'seojun.im@skala.io', role: 'student' },
]

export const roleLabels = {
  student: '교육생',
  instructor: '교수자',
  admin: '관리자',
}
