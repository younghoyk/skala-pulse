<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Badge } from '@/components/ui/badge'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import { mockInstructorCourses } from '@/data/mockInstructorData'
import ScoreBarsCard from '@/components/dashboard/ScoreBarsCard.vue'
import RankLeaderboardCard from '@/components/dashboard/RankLeaderboardCard.vue'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

// 헤더(반/과목명/제출현황)는 실제 담당 강의 데이터. 점수·순위 콘텐츠는 아직 분석
// 도메인 백엔드가 없어서(Phase 4) 역할(전임/실습)에 맞는 mock을 placeholder로 쓴다.
const real = computed(() => courseStore.selectedCourse)
const isLabInstructor = computed(() => real.value?.myRole === 'lab')
const content = computed(() => mockInstructorCourses[isLabInstructor.value ? 'algo-a' : 'vue-a'])

// 실습교수는 본인 점수 막대 1개만, 전임교수는 종합점수(반평균 vs 벤치마크) 2개 막대.
const scoreBars = computed(() => {
  if (!real.value) return []
  const roleColor = getComputedStyle(document.documentElement).getPropertyValue('--role-instructor').trim()
  const inkColor = getComputedStyle(document.documentElement).getPropertyValue('--muted-foreground').trim()
  if (isLabInstructor.value) {
    return [{ label: '실습교수 점수', value: content.value.labScore.value, color: roleColor }]
  }
  return [
    { label: `${real.value.className} 평균`, value: content.value.score.classAvg, color: roleColor },
    { label: '전체 익명 벤치마크', value: content.value.score.benchmarkAvg, color: inkColor },
  ]
})
const lowSampleNote = computed(() => {
  if (!real.value) return ''
  const respondentCount = isLabInstructor.value
    ? content.value.labScore.respondentCount
    : content.value.score.respondentCount
  return respondentCount < content.value.lowSampleThreshold ? '응답 표본 적음 — 신뢰도 낮음' : ''
})

onMounted(() => {
  if (!real.value) router.replace({ name: 'instructor-courses' })
})
</script>

<template>
  <div v-if="real" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        :eyebrow="real.className"
        :title="`${real.courseName} · 대시보드`"
        :back-to="{ name: 'instructor-courses' }"
        back-label="다른 강의 선택"
        accent-var="--role-instructor"
      >
        <template #actions>
          <Badge variant="secondary" class="text-xs">
            제출 현황 {{ real.submission.submitted }}명 / {{ real.submission.total }}명
          </Badge>
        </template>
      </PageHeader>

      <InstructorNav active="dashboard" />

      <!-- 실습교수는 본인 점수만 — 전체 종합(순위 카드 포함)은 전임교수만 본다. -->
      <div v-if="isLabInstructor" class="max-w-sm">
        <ScoreBarsCard title="실습교수 점수" :bars="scoreBars" :low-sample-note="lowSampleNote" />
      </div>
      <!-- 두 카드는 서로 다른 데이터를 병렬로 보여줄 뿐, 인과관계를 연결하는 화살표나
           문구는 두지 않는다 (cause-diagnosis-logic 메모리 원칙). -->
      <div v-else class="grid grid-cols-1 gap-5 md:grid-cols-2">
        <ScoreBarsCard :bars="scoreBars" :low-sample-note="lowSampleNote" />
        <RankLeaderboardCard :items="content.rankItems" />
      </div>
    </div>
  </div>
</template>
