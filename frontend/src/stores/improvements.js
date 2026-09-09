import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 관리자 "개선 과제" 체크리스트 — admin-improvements-not-kanban 메모리 원칙대로 상태
// 필드 없이, 완료 처리는 그냥 서버에서 삭제하는 것과 같다(courses.js와 같은 패턴).
export const useImprovementsStore = defineStore('improvements', () => {
  const improvements = ref([]) // [{id, courseId, courseName, title}]

  async function loadImprovements() {
    improvements.value = await apiFetch('/api/improvements')
  }

  async function addImprovement({ courseId, title }) {
    const created = await apiFetch('/api/improvements', { method: 'POST', body: { courseId, title } })
    improvements.value.push(created)
  }

  async function completeImprovement(id) {
    await apiFetch(`/api/improvements/${id}`, { method: 'DELETE' })
    const idx = improvements.value.findIndex((i) => i.id === id)
    if (idx !== -1) improvements.value.splice(idx, 1)
  }

  return { improvements, loadImprovements, addImprovement, completeImprovement }
})
