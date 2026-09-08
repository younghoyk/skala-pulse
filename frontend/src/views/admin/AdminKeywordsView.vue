<script setup>
import { computed, ref } from 'vue'
import { mockAdminFeedbackByCourse, mergeKeywordGraphs } from '@/data/mockAdminFeedback'
import AdminNav from '@/components/admin/AdminNav.vue'
import KeywordGraphCard from '@/components/dashboard/KeywordGraphCard.vue'

const courseNames = Object.keys(mockAdminFeedbackByCourse)
const selectedCourse = ref('전체')

const merged = computed(() => mergeKeywordGraphs(courseNames.map((name) => mockAdminFeedbackByCourse[name].keywordGraph)))

const activeGraph = computed(() =>
  selectedCourse.value === '전체' ? merged.value.graph : mockAdminFeedbackByCourse[selectedCourse.value].keywordGraph,
)
const activePositions = computed(() =>
  selectedCourse.value === '전체'
    ? merged.value.positions
    : mockAdminFeedbackByCourse[selectedCourse.value].nodePositions,
)
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">전체 과정 키워드</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">과목별로 필터링해서 볼 수 있습니다.</p>

      <AdminNav active="keywords" />

      <div class="mb-5">
        <select
          v-model="selectedCourse"
          class="rounded-lg border border-border bg-card px-3 py-2 text-sm text-foreground outline-none"
        >
          <option value="전체">전체 과목</option>
          <option v-for="name in courseNames" :key="name" :value="name">{{ name }}</option>
        </select>
      </div>

      <KeywordGraphCard :key="selectedCourse" :graph="activeGraph" :positions="activePositions" />
    </div>
  </div>
</template>
