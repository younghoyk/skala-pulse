<script setup>
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip } from 'chart.js'
import { Badge } from '@/components/ui/badge'

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip)

// 범용 막대 비교 카드 — 몇 개의 막대를 보여줄지, 각 막대가 무엇(반/과목전체/벤치마크)인지는
// 호출부가 결정한다. 컴포넌트는 그냥 label/value/color 배열을 그대로 그린다.
const props = defineProps({
  title: { type: String, default: '종합 만족도' },
  bars: { type: Array, required: true }, // [{ label, value, color, submission?: { submitted, total } }]
  lowSampleNote: { type: String, default: '' }, // 비어있지 않으면 배지로 표시
})

const style = getComputedStyle(document.documentElement)
const gridColor = style.getPropertyValue('--border').trim() || '#e5e5e5'
const inkColor = style.getPropertyValue('--muted-foreground').trim() || '#71717a'

const chartData = computed(() => ({
  labels: props.bars.map((b) => b.label),
  datasets: [
    {
      data: props.bars.map((b) => b.value),
      backgroundColor: props.bars.map((b) => b.color),
      borderRadius: 4,
      maxBarThickness: 48,
    },
  ],
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      min: 0,
      max: 5,
      ticks: { color: inkColor, stepSize: 1 },
      grid: { color: gridColor },
    },
    x: {
      ticks: { color: inkColor },
      grid: { display: false },
    },
  },
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label(ctx) {
          const bar = props.bars[ctx.dataIndex]
          const lines = [`만족도 ${ctx.parsed.y.toFixed(1)}`]
          if (bar?.submission) lines.push(`제출 ${bar.submission.submitted}명 / ${bar.submission.total}명`)
          return lines
        },
      },
    },
  },
}
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <div class="mb-3 flex items-center justify-between">
      <h3 class="text-sm font-semibold text-foreground">{{ title }}</h3>
      <Badge v-if="lowSampleNote" variant="secondary" class="text-[10px]">{{ lowSampleNote }}</Badge>
    </div>
    <div style="height: 220px">
      <Bar :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>
