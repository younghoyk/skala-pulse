<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { mockAdminClasses, aggregateRankItems } from '@/data/mockAdminOverview'
import ScoreBarsCard from '@/components/dashboard/ScoreBarsCard.vue'
import RankLeaderboardCard from '@/components/dashboard/RankLeaderboardCard.vue'

const route = useRoute()

const classes = computed(() => mockAdminClasses.filter((c) => c.courseId === route.params.courseId))
const otherClasses = computed(() => mockAdminClasses.filter((c) => c.courseId !== route.params.courseId))
const courseName = computed(() => classes.value[0]?.courseName ?? '')

function average(list) {
  return list.reduce((sum, c) => sum + c.currentAvg, 0) / list.length
}

const roleColor = getComputedStyle(document.documentElement).getPropertyValue('--role-admin').trim()
const inkColor = getComputedStyle(document.documentElement).getPropertyValue('--muted-foreground').trim()

// 반마다 막대 + 이 과목 전체 평균 + 다른 과목들을 다 합친 평균(벤치마크) — 인과관계를
// 계산하거나 연결하지 않고 병치만 한다 (cause-diagnosis-logic 원칙).
const scoreBars = computed(() => [
  ...classes.value.map((c) => ({ label: c.className, value: c.currentAvg, color: roleColor })),
  { label: `${courseName.value} 전체`, value: average(classes.value), color: roleColor },
  { label: '다른 과목 평균', value: average(otherClasses.value), color: inkColor },
])

// 개별 반이 아니라 이 과목 반들의 항목별 순위를 평균 낸 "과목 전체" 순위표 하나.
const courseRankItems = computed(() => aggregateRankItems(classes.value))
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-5">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">{{ courseName }}</h1>
        <RouterLink :to="{ name: 'admin-dashboard' }" class="text-xs underline" style="color: var(--role-admin)">
          전체 강의 목록으로
        </RouterLink>
      </div>

      <div class="grid grid-cols-1 gap-5 md:grid-cols-2">
        <ScoreBarsCard :bars="scoreBars" />
        <RankLeaderboardCard :items="courseRankItems" />
      </div>
    </div>
  </div>
</template>
