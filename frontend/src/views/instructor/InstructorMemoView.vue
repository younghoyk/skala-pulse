<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import ActionMemoCard from '@/components/dashboard/ActionMemoCard.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

const real = computed(() => courseStore.selectedCourse)

onMounted(() => {
  if (!real.value) router.replace({ name: 'instructor-courses' })
})
</script>

<template>
  <div v-if="real" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        :eyebrow="real.className"
        :title="`${real.courseName} · 액션 메모`"
        :back-to="{ name: 'instructor-courses' }"
        back-label="다른 강의 선택"
        accent-var="--role-instructor"
      />

      <InstructorNav active="memo" />

      <ActionMemoCard />
    </div>
  </div>
</template>
