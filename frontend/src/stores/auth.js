import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 실제 Slack OAuth 연동 전까지, 기획서의 "역할별 원클릭 데모 로그인"을 백엔드
// POST /api/auth/demo-login으로 그대로 넘긴다. 교육생은 실제 승인된 회원 중 한 명으로
// 로그인해서(memberId) 반 정보가 그 회원 걸 그대로 따라온다 — 회원가입 때 이미 반을
// 골랐으니 로그인 때 또 고르게 하지 않는다. 교수자/관리자는 특정 회원과 안 묶인
// 자유 입력 데모 로그인 그대로(전임/실습은 로그인이 아니라 반 단위 데이터에 있음,
// mockInstructorData.js의 myRole 참고).
export const useAuthStore = defineStore('auth', () => {
  const role = ref(null) // 'student' | 'instructor' | 'admin' | null
  const displayName = ref('')
  const token = ref('')
  const studentClassName = ref(null) // 교육생 로그인 시 그 회원의 반 — 로그인 응답에서 그대로 옴

  async function loginAsDemo(nextRole, name, memberId) {
    const data = await apiFetch('/api/auth/demo-login', {
      method: 'POST',
      body: { role: nextRole.toUpperCase(), displayName: name, memberId },
    })
    role.value = data.role.toLowerCase()
    displayName.value = data.displayName
    token.value = data.token
    studentClassName.value = data.className ?? null
  }

  function logout() {
    role.value = null
    displayName.value = ''
    token.value = ''
    studentClassName.value = null
  }

  return { role, displayName, token, studentClassName, loginAsDemo, logout }
})
