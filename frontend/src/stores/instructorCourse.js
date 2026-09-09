import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 강사가 실제로 담당하는 과정 목록과, 그중 지금 보고 있는 과정 하나 — 대시보드/키워드/
// 갤러리 화면들이 항상 같은 과정을 보여주고, 분석 결과를 한 번만 불러와 공유하도록
// 이 스토어를 쓴다.
export const useInstructorCourseStore = defineStore('instructorCourse', () => {
  const myCourses = ref([]) // {courseId, courseName, myRole, classes, submission}
  const selectedCourseId = ref(null)
  const analysis = ref(null)
  const analysisLoading = ref(false)

  const selectedCourse = computed(
    () => myCourses.value.find((c) => c.courseId === selectedCourseId.value) ?? null,
  )

  // 헤더 eyebrow에 "1반~5반"(반 여러 개)처럼 붙일 범위 — 화면 4곳이 똑같이 필요로 해서 여기 둔다.
  const selectedClassRange = computed(() => {
    const classes = selectedCourse.value?.classes
    if (!classes) return ''
    return classes.length > 1 ? `${classes[0].className}~${classes[classes.length - 1].className}` : classes[0].className
  })

  async function loadMyCourses() {
    const data = await apiFetch('/api/courses/mine')
    myCourses.value = data.map((c) => ({ ...c, myRole: c.myRole.toLowerCase() }))
  }

  function selectCourse(courseId) {
    selectedCourseId.value = courseId
    analysis.value = null
  }

  async function loadAnalysis(courseId) {
    if (analysis.value?.courseId === courseId) return
    analysisLoading.value = true
    try {
      const data = await apiFetch(`/api/courses/${courseId}/analysis`)
      analysis.value = { ...data, courseId }
    } finally {
      analysisLoading.value = false
    }
  }

  return {
    myCourses,
    selectedCourseId,
    selectedCourse,
    selectedClassRange,
    analysis,
    analysisLoading,
    loadMyCourses,
    selectCourse,
    loadAnalysis,
  }
})
