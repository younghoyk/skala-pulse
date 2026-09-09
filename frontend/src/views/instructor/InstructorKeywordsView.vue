<script setup>
import { computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import KeywordGraphCard from '@/components/dashboard/KeywordGraphCard.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

const real = computed(() => courseStore.selectedCourse)
const hasKeywords = computed(() => (courseStore.analysis?.keywordGraph.nodes.length ?? 0) > 0)

function load() {
  if (!real.value) {
    router.replace({ name: 'instructor-courses' })
    return
  }
  courseStore.loadAnalysis(real.value.courseId)
}

onMounted(load)
watch(() => real.value?.courseId, load)
</script>

<template>
  <div v-if="real" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        :eyebrow="courseStore.selectedClassRange"
        :title="`${real.courseName} · 키워드`"
        :back-to="{ name: 'instructor-courses' }"
        back-label="다른 강의 선택"
        accent-var="--role-instructor"
      />

      <InstructorNav active="keywords" />

      <p v-if="courseStore.analysisLoading" class="py-10 text-center text-sm text-muted-foreground">불러오는 중...</p>
      <KeywordGraphCard
        v-else-if="hasKeywords"
        :graph="courseStore.analysis.keywordGraph"
        :positions="courseStore.analysis.keywordPositions"
      />
      <div v-else class="rounded-xl border border-border bg-card p-5 text-sm text-muted-foreground shadow-sm">
        아직 쌓인 피드백이 없어요.
      </div>
    </div>
  </div>
</template>
