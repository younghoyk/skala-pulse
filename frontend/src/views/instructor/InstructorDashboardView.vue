<script setup>
import { Badge } from '@/components/ui/badge'
import { mockInstructorClass } from '@/data/mockInstructorData'
import ScoreTrendCard from '@/components/dashboard/ScoreTrendCard.vue'
import RankLeaderboardCard from '@/components/dashboard/RankLeaderboardCard.vue'

const data = mockInstructorClass
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-6 flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-xs font-medium text-muted-foreground">{{ data.className }}</p>
          <h1 class="text-xl font-bold text-foreground">{{ data.courseName }} · 대시보드</h1>
        </div>
        <Badge variant="secondary" class="text-xs">
          제출 현황 {{ data.submission.submitted }}명 / {{ data.submission.total }}명
        </Badge>
      </div>

      <!-- 두 카드는 서로 다른 데이터를 병렬로 보여줄 뿐, 인과관계를 연결하는 화살표나
           문구는 두지 않는다 (cause-diagnosis-logic 메모리 원칙). -->
      <div class="grid grid-cols-1 gap-5 md:grid-cols-2">
        <ScoreTrendCard
          :trend="data.scoreTrend"
          :low-sample-threshold="data.lowSampleThreshold"
          :class-label="data.classShortLabel"
        />
        <RankLeaderboardCard :items="data.rankItems" />
      </div>
    </div>
  </div>
</template>
