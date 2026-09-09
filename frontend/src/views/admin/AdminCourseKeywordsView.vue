<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { mockAdminClasses } from '@/data/mockAdminOverview'
import { mockAdminFeedbackByCourse } from '@/data/mockAdminFeedback'
import AdminCourseNav from '@/components/admin/AdminCourseNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import KeywordGraphCard from '@/components/dashboard/KeywordGraphCard.vue'

const route = useRoute()

const courseName = computed(
  () => mockAdminClasses.find((c) => c.courseId === route.params.courseId)?.courseName ?? '',
)
const feedback = computed(() => mockAdminFeedbackByCourse[route.params.courseId] ?? null)
</script>

<template>
  <div v-if="feedback" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        eyebrow="시스템 관리자"
        :title="courseName"
        :back-to="{ name: 'admin-dashboard' }"
        back-label="전체 강의 목록으로"
      />

      <AdminCourseNav :course-id="route.params.courseId" active="keywords" />

      <KeywordGraphCard :graph="feedback.keywordGraph" :positions="feedback.nodePositions" />
    </div>
  </div>
</template>
