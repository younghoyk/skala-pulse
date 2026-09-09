<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Badge } from '@/components/ui/badge'
import { useAuthStore } from '@/stores/auth'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorCourses } from '@/data/mockInstructorData'
import ScoreBarsCard from '@/components/dashboard/ScoreBarsCard.vue'
import RankLeaderboardCard from '@/components/dashboard/RankLeaderboardCard.vue'
import InstructorNav from '@/components/instructor/InstructorNav.vue'

const router = useRouter()
const auth = useAuthStore()
const courseStore = useInstructorCourseStore()

const data = computed(() => mockInstructorCourses[courseStore.selectedCourseId] ?? null)
const isLabInstructor = computed(() => auth.instructorType === 'lab')

// 실습교수는 본인 점수 막대 1개만, 전임교수는 종합점수(반평균 vs 벤치마크) 2개 막대.
const scoreBars = computed(() => {
  if (!data.value) return []
  const roleColor = getComputedStyle(document.documentElement).getPropertyValue('--role-instructor').trim()
  const inkColor = getComputedStyle(document.documentElement).getPropertyValue('--muted-foreground').trim()
  if (isLabInstructor.value) {
    return [{ label: '실습교수 점수', value: data.value.labScore.value, color: roleColor }]
  }
  return [
    { label: `${data.value.classShortLabel} 평균`, value: data.value.score.classAvg, color: roleColor },
    { label: '전체 익명 벤치마크', value: data.value.score.benchmarkAvg, color: inkColor },
  ]
})
const lowSampleNote = computed(() => {
  if (!data.value) return ''
  const respondentCount = isLabInstructor.value ? data.value.labScore.respondentCount : data.value.score.respondentCount
  return respondentCount < data.value.lowSampleThreshold ? '응답 표본 적음 — 신뢰도 낮음' : ''
})

onMounted(() => {
  if (!data.value) router.replace({ name: 'instructor-courses' })
})
</script>

<template>
  <div v-if="data" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-xs font-medium text-muted-foreground">{{ data.className }}</p>
          <h1 class="text-xl font-bold text-foreground">{{ data.courseName }} · 대시보드</h1>
          <RouterLink :to="{ name: 'instructor-courses' }" class="text-xs underline" style="color: var(--role-instructor)">
            다른 강의 선택
          </RouterLink>
        </div>
        <Badge variant="secondary" class="text-xs">
          제출 현황 {{ data.submission.submitted }}명 / {{ data.submission.total }}명
        </Badge>
      </div>

      <InstructorNav active="dashboard" />

      <!-- 실습교수는 본인 점수만 — 전체 종합(순위 카드 포함)은 전임교수만 본다. -->
      <div v-if="isLabInstructor" class="max-w-sm">
        <ScoreBarsCard title="실습교수 점수" :bars="scoreBars" :low-sample-note="lowSampleNote" />
      </div>
      <!-- 두 카드는 서로 다른 데이터를 병렬로 보여줄 뿐, 인과관계를 연결하는 화살표나
           문구는 두지 않는다 (cause-diagnosis-logic 메모리 원칙). -->
      <div v-else class="grid grid-cols-1 gap-5 md:grid-cols-2">
        <ScoreBarsCard :bars="scoreBars" :low-sample-note="lowSampleNote" />
        <RankLeaderboardCard :items="data.rankItems" />
      </div>
    </div>
  </div>
</template>
