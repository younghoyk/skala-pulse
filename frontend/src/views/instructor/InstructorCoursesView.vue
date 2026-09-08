<script setup>
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorCourses } from '@/data/mockInstructorData'

const router = useRouter()
const courseStore = useInstructorCourseStore()

const courses = Object.values(mockInstructorCourses)

function pickCourse(id) {
  courseStore.selectCourse(id)
  router.push({ name: 'instructor-dashboard' })
}

function latestScore(course) {
  return course.scoreTrend[course.scoreTrend.length - 1].classAvg
}
</script>

<template>
  <div class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-md">
      <p class="mb-1 text-xs font-medium text-muted-foreground">교수자 / 매니저</p>
      <h1 class="mb-1 text-xl font-bold text-foreground">담당 강의를 선택해주세요</h1>
      <p class="mb-6 text-sm text-muted-foreground">여러 강의를 담당 중이면 강의별로 대시보드가 따로 표시돼요.</p>

      <div class="flex flex-col gap-3">
        <button
          v-for="course in courses"
          :key="course.id"
          type="button"
          class="flex items-center justify-between rounded-lg border border-border bg-card px-4 py-3.5 text-left transition hover:shadow-sm"
          style="--hover-border: var(--role-instructor)"
          @mouseenter="(e) => (e.currentTarget.style.borderColor = 'var(--role-instructor)')"
          @mouseleave="(e) => (e.currentTarget.style.borderColor = '')"
          @click="pickCourse(course.id)"
        >
          <div>
            <p class="text-xs text-muted-foreground">{{ course.className }}</p>
            <p class="text-sm font-semibold text-foreground">{{ course.courseName }}</p>
            <p class="mt-0.5 text-xs text-muted-foreground">
              제출 {{ course.submission.submitted }}명 / {{ course.submission.total }}명
            </p>
          </div>
          <span
            class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white"
            style="background: var(--role-instructor)"
          >
            {{ latestScore(course).toFixed(1) }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>
