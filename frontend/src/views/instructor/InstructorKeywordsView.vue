<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorCourses } from '@/data/mockInstructorData'
import { mockInstructorFeedbackByCourse } from '@/data/mockInstructorFeedback'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import KeywordGraphCard from '@/components/dashboard/KeywordGraphCard.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

const data = computed(() => mockInstructorCourses[courseStore.selectedCourseId] ?? null)
const feedback = computed(() => mockInstructorFeedbackByCourse[courseStore.selectedCourseId] ?? null)

onMounted(() => {
  if (!data.value) router.replace({ name: 'instructor-courses' })
})
</script>

<template>
  <div v-if="data" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        :eyebrow="data.className"
        :title="`${data.courseName} · 키워드`"
        :back-to="{ name: 'instructor-courses' }"
        back-label="다른 강의 선택"
        accent-var="--role-instructor"
      />

      <InstructorNav active="keywords" />

      <KeywordGraphCard :graph="feedback.keywordGraph" :positions="feedback.nodePositions" />
    </div>
  </div>
</template>
