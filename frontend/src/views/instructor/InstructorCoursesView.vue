<script setup>
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorCourses } from '@/data/mockInstructorData'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

const courses = Object.values(mockInstructorCourses)

function pickCourse(id) {
  courseStore.selectCourse(id)
  router.push({ name: 'instructor-dashboard' })
}

function latestScore(course) {
  return course.score.classAvg
}

const roleLabel = { lead: '전임교수', lab: '실습교수' }
</script>

<template>
  <div class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-md">
      <PageHeader
        eyebrow="교수자 / 매니저"
        title="담당 강의를 선택해주세요"
        description="여러 강의를 담당 중이면 강의별로 대시보드가 따로 표시돼요."
        accent-var="--role-instructor"
      />

      <div class="flex flex-col gap-3">
        <button
          v-for="course in courses"
          :key="course.id"
          type="button"
          class="flex items-center justify-between rounded-lg border border-border bg-card px-4 py-3.5 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
          style="--hover-border: var(--role-instructor)"
          @mouseenter="(e) => (e.currentTarget.style.borderColor = 'var(--role-instructor)')"
          @mouseleave="(e) => (e.currentTarget.style.borderColor = '')"
          @click="pickCourse(course.id)"
        >
          <div>
            <div class="flex items-center gap-1.5">
              <p class="text-xs text-muted-foreground">{{ course.className }}</p>
              <span
                class="rounded-full px-1.5 py-0.5 text-[10px] font-medium"
                style="background: var(--role-instructor-soft); color: var(--role-instructor)"
              >
                {{ roleLabel[course.myRole] }}
              </span>
            </div>
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
