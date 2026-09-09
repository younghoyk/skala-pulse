import { defineStore } from 'pinia'
import { ref } from 'vue'

// 실제 Slack OAuth/JWT 연동 전까지, 기획서의 "역할별 원클릭 데모 로그인"을 대신한다.
// 강사의 전임/실습 구분은 로그인 단계가 아니라 반(강의) 단위 데이터에 있다 —
// 같은 사람이 이 반에선 전임, 저 반에선 실습일 수 있어서(mockInstructorData.js의
// myRole 참고) 로그인 시점엔 role만 필요하다.
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
