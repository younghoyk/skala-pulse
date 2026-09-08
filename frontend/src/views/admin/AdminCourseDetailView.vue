<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { mockAdminClasses } from '@/data/mockAdminOverview'
import ClassOverviewRow from '@/components/dashboard/ClassOverviewRow.vue'

const route = useRoute()

const classes = computed(() => mockAdminClasses.filter((c) => c.courseId === route.params.courseId))
const courseName = computed(() => classes.value[0]?.courseName ?? '')

// 이 과목 안에서도 하락폭이 큰 반부터 정렬한다 — 바깥 대시보드와 같은 원칙.
const sortedClasses = computed(() =>
  [...classes.value].sort((a, b) => a.currentAvg - a.previousAvg - (b.currentAvg - b.previousAvg)),
)
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">{{ courseName }}</h1>
        <RouterLink :to="{ name: 'admin-dashboard' }" class="text-xs underline" style="color: var(--role-admin)">
          전체 강의 목록으로
        </RouterLink>
      </div>
      <p class="mb-5 mt-3 text-xs text-muted-foreground">지난 기간 대비 하락폭이 큰 반부터 정렬했습니다.</p>

      <div class="flex flex-col gap-2.5">
        <ClassOverviewRow v-for="c in sortedClasses" :key="c.id" :class-data="c" />
      </div>
    </div>
  </div>
</template>
