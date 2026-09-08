<script setup>
import { computed, onMounted } from 'vue'

const props = defineProps({
  question: { type: String, required: true },
  min: { type: Number, default: 0 },
  max: { type: Number, default: 5 },
  modelValue: { type: Number, default: null },
})
const emit = defineEmits(['update:modelValue'])

// 0~5 사이를 라디오처럼 6단계로 끊지 않고 연속값으로 드래그할 수 있게 한다.
const STEP = 0.1

function onInput(e) {
  emit('update:modelValue', Number(e.target.value))
}

onMounted(() => {
  if (props.modelValue === null) {
    emit('update:modelValue', (props.min + props.max) / 2)
  }
})

// 값 구간에 따라 표정을 렌더링 — 숫자 대신 직관적으로 만족도를 표현한다.
const FACES = ['😞', '🙁', '😐', '🙂', '😄', '🤩']
const face = computed(() => {
  if (props.modelValue === null) return '❔'
  const ratio = (props.modelValue - props.min) / (props.max - props.min)
  const idx = Math.min(FACES.length - 1, Math.max(0, Math.round(ratio * (FACES.length - 1))))
  return FACES[idx]
})
</script>

<template>
  <div>
    <h2 class="mb-1 text-lg font-semibold text-foreground">{{ question }}</h2>
    <p class="mb-8 text-sm text-muted-foreground">슬라이더를 드래그해서 만족도를 표현해주세요.</p>

    <div class="flex flex-col items-center gap-6 py-4">
      <div
        class="flex h-24 w-24 items-center justify-center rounded-full text-5xl transition-colors"
        :style="{ background: modelValue === null ? 'var(--muted)' : 'var(--role-student-soft)' }"
      >
        {{ face }}
      </div>

      <input
        type="range"
        :min="min"
        :max="max"
        :step="STEP"
        :value="modelValue ?? (min + max) / 2"
        class="score-slider w-full max-w-sm"
        @input="onInput"
      />

      <div class="flex w-full max-w-sm justify-between px-0.5 font-mono text-xs text-muted-foreground">
        <span>아쉬워요</span>
        <span>좋았어요</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.score-slider {
  -webkit-appearance: none;
  appearance: none;
  height: 10px;
  border-radius: 999px;
  background: var(--role-student-soft);
  outline: none;
}

.score-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: var(--role-student);
  border: 3px solid var(--card);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
  cursor: grab;
}

.score-slider::-webkit-slider-thumb:active {
  cursor: grabbing;
}

.score-slider::-moz-range-thumb {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: var(--role-student);
  border: 3px solid var(--card);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
  cursor: grab;
}
</style>
