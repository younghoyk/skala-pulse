<script setup>
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
} from 'chart.js'
import { Badge } from '@/components/ui/badge'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend)

const props = defineProps({
  trend: { type: Array, required: true }, // [{ week, classAvg, benchmarkAvg, respondentCount }]
  lowSampleThreshold: { type: Number, default: 5 },
})

const style = getComputedStyle(document.documentElement)
const roleColor = style.getPropertyValue('--role-instructor').trim() || '#b8730f'
const gridColor = style.getPropertyValue('--border').trim() || '#e5e5e5'
const inkColor = style.getPropertyValue('--muted-foreground').trim() || '#71717a'

const latest = computed(() => props.trend[props.trend.length - 1])
const isLowSample = computed(() => latest.value && latest.value.respondentCount < props.lowSampleThreshold)

const chartData = computed(() => ({
  labels: props.trend.map((p) => p.week),
  datasets: [
    {
      label: '우리 반 평균',
      data: props.trend.map((p) => p.classAvg),
      borderColor: roleColor,
      backgroundColor: roleColor,
      pointRadius: 4,
      pointHoverRadius: 5,
      tension: 0.25,
    },
    {
      label: '전체 익명 벤치마크',
      data: props.trend.map((p) => p.benchmarkAvg),
      borderColor: inkColor,
      backgroundColor: inkColor,
      borderDash: [5, 4],
      pointRadius: 3,
      tension: 0.25,
    },
  ],
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  interaction: { mode: 'index', intersect: false },
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
    legend: { position: 'bottom', labels: { color: inkColor, usePointStyle: true, boxWidth: 8 } },
  },
}
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <div class="mb-3 flex items-center justify-between">
      <h3 class="text-sm font-semibold text-foreground">종합 만족도 추이</h3>
      <Badge v-if="isLowSample" variant="secondary" class="text-[10px]">응답 표본 적음 — 신뢰도 낮음</Badge>
    </div>
    <div style="height: 220px">
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>
