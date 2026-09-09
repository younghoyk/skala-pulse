<script setup>
import { computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Badge } from '@/components/ui/badge'
import { useInstructorCourseStore } from '@/stores/instructorCourse'
import ScoreBarsCard from '@/components/dashboard/ScoreBarsCard.vue'
import RankLeaderboardCard from '@/components/dashboard/RankLeaderboardCard.vue'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const courseStore = useInstructorCourseStore()

const real = computed(() => courseStore.selectedCourse)
const isLabInstructor = computed(() => real.value?.myRole === 'lab')

const LOW_SAMPLE_THRESHOLD = 3

// 실습교수는 본인 반 점수 막대 1개만, 전임교수는 담당 반 전체(최대 5개) + 전체
// 익명 벤치마크 막대. cause-diagnosis-logic 메모리 원칙대로 두 값은 병렬 비교만 하고
// 인과관계를 연결하는 문구는 두지 않는다.
const scoreBars = computed(() => {
  const analysis = courseStore.analysis
  if (!real.value || !analysis) return []
  const roleColor = getComputedStyle(document.documentElement).getPropertyValue('--role-instructor').trim()
  const inkColor = getComputedStyle(document.documentElement).getPropertyValue('--muted-foreground').trim()

  if (isLabInstructor.value) {
    return [{
      label: '실습교수 점수',
      value: analysis.myLabScore ?? 0,
      color: roleColor,
      submission: { submitted: analysis.myLabRespondentCount, total: real.value.submission.total },
    }]
  }

  const classBars = analysis.classScores.map((c) => ({
    label: c.className,
    value: c.avgScore ?? 0,
    color: roleColor,
  }))
  classBars.push({ label: '전체 익명 벤치마크', value: analysis.benchmarkAvgScore ?? 0, color: inkColor })
  return classBars
})

const lowSampleNote = computed(() => {
  const analysis = courseStore.analysis
  if (!analysis) return ''
  const respondentCount = isLabInstructor.value ? analysis.myLabRespondentCount : analysis.courseRespondentCount
  return respondentCount < LOW_SAMPLE_THRESHOLD ? '응답 표본 적음 — 신뢰도 낮음' : ''
})

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

      <p v-if="courseStore.analysisLoading" class="py-10 text-center text-sm text-muted-foreground">불러오는 중...</p>
      <template v-else-if="courseStore.analysis">
        <!-- 실습교수는 본인 점수만 — 전체 종합(순위 카드 포함)은 전임교수만 본다. -->
        <div v-if="isLabInstructor" class="max-w-sm">
          <ScoreBarsCard title="실습교수 점수" :bars="scoreBars" :low-sample-note="lowSampleNote" />
        </div>
        <div v-else class="grid grid-cols-1 gap-5 md:grid-cols-2">
          <ScoreBarsCard title="반별 종합 만족도" :bars="scoreBars" :low-sample-note="lowSampleNote" />
          <RankLeaderboardCard v-if="courseStore.analysis.rankItems.length" :items="courseStore.analysis.rankItems" />
          <div v-else class="rounded-xl border border-border bg-card p-5 text-sm text-muted-foreground shadow-sm">
            아직 쌓인 설문 응답이 없어요.
          </div>
        </div>
      </template>
    </div>
  </div>
</template>
