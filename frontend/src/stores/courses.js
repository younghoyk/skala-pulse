import { defineStore } from 'pinia'
import { reactive } from 'vue'
import { apiFetch } from '@/lib/api'

// 백엔드 format은 대문자(ZOOM/DIRECT), 프론트 기존 화면은 소문자(zoom/direct)를 쓰던
// 컨벤션 그대로 유지 — 변환은 스토어 경계에서만 한다. labInstructorName은 백엔드가 이미
// "직강이면 전임교수와 동일" 규칙을 계산해서 내려주므로 프론트는 그대로 표시만 하면 된다.
function classFromApi(c) {
  return {
    id: c.id,
    name: c.name,
    capacity: c.capacity,
    format: c.format.toLowerCase(),
    labInstructorId: c.labInstructorId,
    labInstructorName: c.labInstructorName,
    startDate: c.startDate,
    endDate: c.endDate,
    submission: c.submission,
  }
}
function courseFromApi(course) {
  return {
    id: course.id,
    name: course.name,
    leadInstructorId: course.leadInstructorId,
    leadInstructorName: course.leadInstructorName,
    classes: course.classes.map(classFromApi),
  }
}

export const useCoursesStore = defineStore('courses', () => {
  const courses = reactive([])

  async function loadCourses() {
    const data = await apiFetch('/api/courses')
    courses.splice(0, courses.length, ...data.map(courseFromApi))
  }

  async function addCourse({ name, leadInstructorId }) {
    const created = await apiFetch('/api/courses', { method: 'POST', body: { name, leadInstructorId } })
    courses.push(courseFromApi(created))
  }

  async function updateCourse(courseId, { leadInstructorId }) {
    const updated = await apiFetch(`/api/courses/${courseId}`, { method: 'PATCH', body: { leadInstructorId } })
    const course = courses.find((c) => c.id === courseId)
    if (course) {
      course.leadInstructorId = updated.leadInstructorId
      course.leadInstructorName = updated.leadInstructorName
    }
  }

  async function deleteCourse(courseId) {
    await apiFetch(`/api/courses/${courseId}`, { method: 'DELETE' })
    const idx = courses.findIndex((c) => c.id === courseId)
    if (idx !== -1) courses.splice(idx, 1)
  }

  async function addClass(courseId, { name, capacity, format, labInstructorId, startDate, endDate }) {
    const updated = await apiFetch(`/api/courses/${courseId}/classes`, {
      method: 'POST',
      body: { name, capacity, format: format.toUpperCase(), labInstructorId, startDate, endDate },
    })
    const course = courses.find((c) => c.id === courseId)
    if (course) course.classes = courseFromApi(updated).classes
  }

  async function updateClass(classId, fields) {
    const body = { ...fields }
    if (body.format) body.format = body.format.toUpperCase()
    const updated = await apiFetch(`/api/classes/${classId}`, { method: 'PATCH', body })
    for (const course of courses) {
      const cls = course.classes.find((c) => c.id === classId)
      if (cls) Object.assign(cls, classFromApi(updated))
    }
  }

  async function deleteClass(courseId, classId) {
    await apiFetch(`/api/classes/${classId}`, { method: 'DELETE' })
    const course = courses.find((c) => c.id === courseId)
    if (course) {
      const idx = course.classes.findIndex((c) => c.id === classId)
      if (idx !== -1) course.classes.splice(idx, 1)
    }
  }

  return { courses, loadCourses, addCourse, updateCourse, deleteCourse, addClass, updateClass, deleteClass }
})
