<script setup>
import { computed } from 'vue'
import { mockAdminClasses } from '@/data/mockAdminOverview'
import ClassOverviewRow from '@/components/dashboard/ClassOverviewRow.vue'
import AdminNav from '@/components/admin/AdminNav.vue'

// 하이라이트는 계산이 아니라 정렬로만 만든다 — 하락폭이 큰 반이 위로 올라올 뿐,
// "왜" 떨어졌는지는 이 화면에서 말하지 않는다 (cause-diagnosis-logic 원칙).
const sortedClasses = computed(() =>
  [...mockAdminClasses].sort(
    (a, b) => a.currentAvg - a.previousAvg - (b.currentAvg - b.previousAvg),
  ),
)
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">전체 과정 통합 대시보드</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">지난 기간 대비 하락폭이 큰 반부터 정렬했습니다.</p>

      <AdminNav active="dashboard" />

      <div class="flex flex-col gap-2.5">
        <ClassOverviewRow v-for="c in sortedClasses" :key="c.id" :class-data="c" />
      </div>
    </div>
  </div>
</template>
