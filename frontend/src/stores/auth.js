import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 이메일+비밀번호로 실제 로그인한다 — 역할은 고르는 게 아니라 서버가 그 계정의
// 실제 역할을 응답에 실어 보내준다. 그 이후(JWT 발급/검증/라우터 가드)는 그대로다.
export const useAuthStore = defineStore('auth', () => {
  const role = ref(null) // 'student' | 'instructor' | 'admin' | null
  const displayName = ref('')
  const token = ref('')
  const studentClassName = ref(null) // 교육생 로그인 시 그 회원의 반 — 로그인 응답에서 그대로 옴

  async function login(email, password) {
    const data = await apiFetch('/api/auth/login', {
      method: 'POST',
      body: { email, password },
    })
    role.value = data.role.toLowerCase()
    displayName.value = data.displayName
    token.value = data.token
    studentClassName.value = data.className ?? null
    return role.value
  }

  function logout() {
    role.value = null
    displayName.value = ''
    token.value = ''
    studentClassName.value = null
  }

  return { role, displayName, token, studentClassName, login, logout }
})
