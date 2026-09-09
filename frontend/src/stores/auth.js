import { defineStore } from 'pinia'
import { ref } from 'vue'

// 실제 Slack OAuth/JWT 연동 전까지, 기획서의 "역할별 원클릭 데모 로그인"을 대신한다.
export const useAuthStore = defineStore('auth', () => {
  const role = ref(null) // 'student' | 'instructor' | 'admin' | null
  const displayName = ref('')
  const instructorType = ref(null) // 'lead'(전임) | 'lab'(실습) | null — role이 'instructor'일 때만 의미 있음

  function loginAsDemo(nextRole, name, nextInstructorType = null) {
    role.value = nextRole
    displayName.value = name
    instructorType.value = nextInstructorType
  }

  function logout() {
    role.value = null
    displayName.value = ''
    instructorType.value = null
  }

  return { role, displayName, instructorType, loginAsDemo, logout }
})
