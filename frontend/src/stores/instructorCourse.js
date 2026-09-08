import { defineStore } from 'pinia'
import { ref } from 'vue'

// 강사가 담당하는 여러 강의 중 지금 보고 있는 강의 하나 — 대시보드/피드백 화면이
// 항상 같은 강의를 보여주도록 이 스토어를 공유한다.
export const useInstructorCourseStore = defineStore('instructorCourse', () => {
  const selectedCourseId = ref(null)

  function selectCourse(id) {
    selectedCourseId.value = id
  }

  return { selectedCourseId, selectCourse }
})
