<script setup>
import { useRouter } from 'vue-router'
import { groupClassesByCourse } from '@/data/mockAdminOverview'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'

const router = useRouter()
const courses = groupClassesByCourse()

function openCourse(courseId) {
  router.push({ name: 'admin-course-detail', params: { courseId } })
}
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">전체 과정 통합 대시보드</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">강의를 선택하면 그 안의 반들을 비교할 수 있습니다.</p>

      <AdminTopNav active="dashboard" />

      <div class="flex flex-col gap-2.5">
        <button
          v-for="course in courses"
          :key="course.courseId"
          type="button"
          class="flex items-center justify-between rounded-lg border border-border bg-card px-4 py-3.5 text-left transition hover:shadow-sm"
          @click="openCourse(course.courseId)"
        >
          <div>
            <p class="text-sm font-semibold text-foreground">{{ course.courseName }}</p>
            <p class="mt-0.5 text-xs text-muted-foreground">{{ course.classes.length }}개 반</p>
          </div>
          <span class="text-xs text-muted-foreground">
            제출 {{ course.submission.submitted }}명 / {{ course.submission.total }}명
          </span>
        </button>
      </div>
    </div>
  </div>
</template>
