import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 강사가 실제로 담당하는 반 목록과, 그중 지금 보고 있는 반 하나 — 대시보드/피드백
// 화면들이 항상 같은 반을 보여주도록 이 스토어를 공유한다.
export const useInstructorCourseStore = defineStore('instructorCourse', () => {
  const myCourses = ref([]) // {classId, courseName, className, myRole, submission}
  const selectedClassId = ref(null)

  const selectedCourse = computed(
    () => myCourses.value.find((c) => c.classId === selectedClassId.value) ?? null,
  )

  async function loadMyCourses() {
    const data = await apiFetch('/api/courses/mine')
    myCourses.value = data.map((c) => ({ ...c, myRole: c.myRole.toLowerCase() }))
  }

  function selectCourse(classId) {
    selectedClassId.value = classId
  }

  return { myCourses, selectedClassId, selectedCourse, loadMyCourses, selectCourse }
})
