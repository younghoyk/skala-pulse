<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorCourses } from '@/data/mockInstructorData'
import { mockInstructorFeedbackByCourse } from '@/data/mockInstructorFeedback'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import FeedbackGalleryCard from '@/components/dashboard/FeedbackGalleryCard.vue'

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
      <div class="mb-4">
        <p class="text-xs font-medium text-muted-foreground">{{ data.className }}</p>
        <h1 class="text-xl font-bold text-foreground">{{ data.courseName }} · 피드백 갤러리</h1>
        <RouterLink :to="{ name: 'instructor-courses' }" class="text-xs underline" style="color: var(--role-instructor)">
          다른 강의 선택
        </RouterLink>
      </div>

      <InstructorNav active="gallery" />

      <FeedbackGalleryCard :items="feedback.feedbackGallery" />
    </div>
  </div>
</template>
