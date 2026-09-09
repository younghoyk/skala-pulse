import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 실제 Slack OAuth 연동 전까지, 기획서의 "역할별 원클릭 데모 로그인"을 백엔드
// POST /api/auth/demo-login으로 그대로 넘긴다 — 특정 회원 레코드와 묶이지 않는,
// 역할만 있는 세션(JWT)이다. 강사의 전임/실습 구분은 로그인 단계가 아니라 반(강의)
// 단위 데이터에 있다 — 같은 사람이 이 반에선 전임, 저 반에선 실습일 수 있어서
// (mockInstructorData.js의 myRole 참고) 로그인 시점엔 role만 필요하다.
export const useAuthStore = defineStore('auth', () => {
  const role = ref(null) // 'student' | 'instructor' | 'admin' | null
  const displayName = ref('')
  const token = ref('')

  async function loginAsDemo(nextRole, name) {
    const data = await apiFetch('/api/auth/demo-login', {
      method: 'POST',
      body: { role: nextRole.toUpperCase(), displayName: name },
    })
    role.value = data.role.toLowerCase()
    displayName.value = data.displayName
    token.value = data.token
  }

  function logout() {
    role.value = null
    displayName.value = ''
    token.value = ''
  }

  return { role, displayName, token, loginAsDemo, logout }
})
