<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, required: true }, // [{ id, label, avgRank, previousAvgRank }]
})

function toPositions(list, key) {
  return [...list]
    .sort((a, b) => a[key] - b[key])
    .map((item, idx) => ({ id: item.id, position: idx + 1 }))
}

// 소수점 avgRank 값은 화면에 노출하지 않고, 정렬된 순위(1위~N위)와 지난 기간 대비
// 순위 변동만 계산해서 보여준다 — cause-diagnosis-logic 메모리의 표시 원칙.
const leaderboard = computed(() => {
  const currentPositions = toPositions(props.items, 'avgRank')
  const previousPositions = toPositions(props.items, 'previousAvgRank')
  const prevMap = Object.fromEntries(previousPositions.map((p) => [p.id, p.position]))

  return currentPositions.map((entry) => {
    const item = props.items.find((i) => i.id === entry.id)
    const prevPosition = prevMap[entry.id]
    const delta = prevPosition - entry.position // 양수 = 순위 상승(개선)
    return { ...item, position: entry.position, delta }
  })
})

const lowestPosition = computed(() => Math.max(...props.items.map((_, idx) => idx + 1)))
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <h3 class="mb-3 text-sm font-semibold text-foreground">세부 항목 순위</h3>
    <p class="mb-4 text-xs text-muted-foreground">지난 기간 대비 순위 변동 · 최하위 항목만 강조</p>

    <ol class="flex flex-col gap-1.5">
      <li
        v-for="entry in leaderboard"
        :key="entry.id"
        class="flex items-center gap-3 rounded-lg px-3 py-2.5"
        :class="entry.position === lowestPosition ? 'border' : ''"
        :style="entry.position === lowestPosition ? { background: 'var(--danger-soft)', borderColor: 'var(--danger)' } : {}"
      >
        <span
          class="flex h-6 w-6 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white"
          style="background: var(--role-instructor)"
        >
          {{ entry.position }}
        </span>
        <span class="flex-1 text-sm font-medium text-foreground">{{ entry.label }}</span>

        <span
          v-if="entry.delta > 0"
          class="flex items-center gap-0.5 font-mono text-xs font-semibold"
          style="color: var(--role-student)"
        >
          ▲ {{ entry.delta }}
        </span>
        <span
          v-else-if="entry.delta < 0"
          class="flex items-center gap-0.5 font-mono text-xs font-semibold"
          style="color: var(--danger)"
        >
          ▼ {{ -entry.delta }}
        </span>
        <span v-else class="font-mono text-xs text-muted-foreground">–</span>
      </li>
    </ol>
  </div>
</template>
