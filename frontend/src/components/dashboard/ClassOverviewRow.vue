<script setup>
import { computed } from 'vue'
import { Badge } from '@/components/ui/badge'

const props = defineProps({
  classData: { type: Object, required: true },
})

const delta = computed(() => +(props.classData.currentAvg - props.classData.previousAvg).toFixed(2))
const isLowSample = computed(() => props.classData.respondentCount < props.classData.lowSampleThreshold)
</script>

<template>
  <div class="flex flex-wrap items-center gap-4 rounded-lg border border-border bg-card px-4 py-3.5">
    <div class="min-w-[140px] flex-1">
      <p class="text-xs text-muted-foreground">{{ classData.courseName }}</p>
      <p class="text-sm font-semibold text-foreground">{{ classData.className }}</p>
    </div>

    <div class="flex items-center gap-2">
      <span
        class="flex h-10 w-10 items-center justify-center rounded-full text-sm font-bold text-white"
        style="background: var(--role-admin)"
      >
        {{ classData.currentAvg.toFixed(1) }}
      </span>
      <span
        v-if="delta < 0"
        class="font-mono text-xs font-semibold"
        style="color: var(--danger)"
      >
        ▼ {{ Math.abs(delta).toFixed(1) }}
      </span>
      <span v-else-if="delta > 0" class="font-mono text-xs font-semibold" style="color: var(--role-student)">
        ▲ {{ delta.toFixed(1) }}
      </span>
      <span v-else class="font-mono text-xs text-muted-foreground">–</span>
    </div>

    <span
      class="rounded-full px-2.5 py-1 text-[11px] font-medium"
      style="background: var(--role-admin-soft); color: var(--role-admin)"
    >
      최하위 항목 · {{ classData.worstItemLabel }}
    </span>

    <span class="text-xs text-muted-foreground">
      제출 {{ classData.submission.submitted }}명 / {{ classData.submission.total }}명
    </span>

    <Badge v-if="isLowSample" variant="secondary" class="text-[10px]">신뢰도 낮음</Badge>
  </div>
</template>
