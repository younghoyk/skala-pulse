<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorFeedbackByCourse } from '@/data/mockInstructorFeedback'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import FeedbackGalleryCard from '@/components/dashboard/FeedbackGalleryCard.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

// 헤더는 실제 담당 강의, 피드백 갤러리는 아직 분석 도메인 백엔드가 없어서 역할별 mock.
const real = computed(() => courseStore.selectedCourse)
const feedback = computed(
  () => mockInstructorFeedbackByCourse[real.value?.myRole === 'lab' ? 'algo-a' : 'vue-a'],
)

onMounted(() => {
  if (!real.value) router.replace({ name: 'instructor-courses' })
})
</script>

<template>
  <div v-if="real" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        :eyebrow="real.className"
        :title="`${real.courseName} · 피드백 갤러리`"
        :back-to="{ name: 'instructor-courses' }"
        back-label="다른 강의 선택"
        accent-var="--role-instructor"
      />

      <InstructorNav active="gallery" />

      <FeedbackGalleryCard :items="feedback.feedbackGallery" />
    </div>
  </div>
</template>
