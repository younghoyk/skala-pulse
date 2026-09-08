<script setup>
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip } from 'chart.js'
import { Badge } from '@/components/ui/badge'

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip)

// 설문은 강의 종료 시 한 번만 진행되는 최종 조사라, 여러 기수를 이어붙인 추이가 아니라
// 이번 기수의 "반 평균 vs 전체 익명 벤치마크" 딱 2개 막대만 보여준다.
const props = defineProps({
  score: { type: Object, required: true }, // { classAvg, benchmarkAvg, respondentCount }
  lowSampleThreshold: { type: Number, default: 5 },
  classLabel: { type: String, default: '이 반' }, // 강사가 여러 반을 담당할 수 있어 "우리 반" 대신 구체적인 이름을 받는다
})

const style = getComputedStyle(document.documentElement)
const roleColor = style.getPropertyValue('--role-instructor').trim() || '#b8730f'
const gridColor = style.getPropertyValue('--border').trim() || '#e5e5e5'
const inkColor = style.getPropertyValue('--muted-foreground').trim() || '#71717a'

const isLowSample = computed(() => props.score.respondentCount < props.lowSampleThreshold)

const chartData = computed(() => ({
  labels: [`${props.classLabel} 평균`, '전체 익명 벤치마크'],
  datasets: [
    {
      data: [props.score.classAvg, props.score.benchmarkAvg],
      backgroundColor: [roleColor, inkColor],
      borderRadius: 4,
      maxBarThickness: 56,
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
  },
}
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <div class="mb-3 flex items-center justify-between">
      <h3 class="text-sm font-semibold text-foreground">종합 만족도</h3>
      <Badge v-if="isLowSample" variant="secondary" class="text-[10px]">응답 표본 적음 — 신뢰도 낮음</Badge>
    </div>
    <div style="height: 220px">
      <Bar :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>
