import { defineStore } from 'pinia'
import { ref } from 'vue'

// 실제 Slack OAuth/JWT 연동 전까지, 기획서의 "역할별 원클릭 데모 로그인"을 대신한다.
export const useAuthStore = defineStore('auth', () => {
  const role = ref(null) // 'student' | 'instructor' | 'admin' | null
  const displayName = ref('')

  function loginAsDemo(nextRole, name) {
    role.value = nextRole
    displayName.value = name
  }

  function logout() {
    role.value = null
    displayName.value = ''
  }

  return { role, displayName, loginAsDemo, logout }
})
