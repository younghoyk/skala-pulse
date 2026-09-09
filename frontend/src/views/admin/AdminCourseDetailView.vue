<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { apiFetch } from '@/lib/api'
import ScoreBarsCard from '@/components/dashboard/ScoreBarsCard.vue'
import RankLeaderboardCard from '@/components/dashboard/RankLeaderboardCard.vue'
import AdminCourseNav from '@/components/admin/AdminCourseNav.vue'
import PageHeader from '@/components/PageHeader.vue'

const route = useRoute()
const analysis = ref(null)
const loading = ref(false)

async function load() {
  loading.value = true
  analysis.value = null
  try {
    analysis.value = await apiFetch(`/api/courses/${route.params.courseId}/analysis`)
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => route.params.courseId, load)

const roleColor = getComputedStyle(document.documentElement).getPropertyValue('--role-admin').trim()
const inkColor = getComputedStyle(document.documentElement).getPropertyValue('--muted-foreground').trim()

// 반마다 막대 + 이 과목 전체 평균 + 다른 과목들을 다 합친 평균(벤치마크) — 인과관계를
// 계산하거나 연결하지 않고 병치만 한다 (cause-diagnosis-logic 원칙).
const scoreBars = computed(() => {
  if (!analysis.value) return []
  const bars = analysis.value.classScores.map((c) => ({ label: c.className, value: c.avgScore ?? 0, color: roleColor }))
  bars.push({ label: `${analysis.value.courseName} 전체`, value: analysis.value.courseAvgScore ?? 0, color: roleColor })
  bars.push({ label: '다른 과목 평균', value: analysis.value.benchmarkAvgScore ?? 0, color: inkColor })
  return bars
})
</script>

<template>
  <div v-if="analysis" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        eyebrow="시스템 관리자"
        :title="analysis.courseName"
        :back-to="{ name: 'admin-dashboard' }"
        back-label="전체 강의 목록으로"
      />

      <AdminCourseNav :course-id="route.params.courseId" active="dashboard" />

      <div class="grid grid-cols-1 gap-5 md:grid-cols-2">
        <ScoreBarsCard :bars="scoreBars" />
        <RankLeaderboardCard v-if="analysis.rankItems.length" :items="analysis.rankItems" />
        <div v-else class="rounded-xl border border-border bg-card p-5 text-sm text-muted-foreground shadow-sm">
          아직 쌓인 설문 응답이 없어요.
        </div>
      </div>
    </div>
  </div>
  <p v-else-if="loading" class="py-10 text-center text-sm text-muted-foreground">불러오는 중...</p>
</template>
