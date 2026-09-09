<script setup>
const props = defineProps({
  question: { type: String, required: true },
  modelValue: { type: String, default: '' },
})
const emit = defineEmits(['update:modelValue'])

const MAX_LENGTH = 100

function onInput(e) {
  emit('update:modelValue', e.target.value)
}
</script>

<template>
  <div>
    <h2 class="mb-1 text-lg font-semibold text-foreground">{{ question }}</h2>
    <p class="mb-6 text-sm text-muted-foreground">한 줄로 짧게 답해주세요 (선택 입력).</p>

    <input
      type="text"
      :value="modelValue"
      :maxlength="MAX_LENGTH"
      placeholder="답변을 입력해주세요"
      class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 block w-full rounded-lg border bg-transparent px-2.5 py-1.5 text-base outline-none placeholder:text-muted-foreground focus-visible:ring-3 md:text-sm"
      @input="onInput"
    />
    <p class="mt-1 text-right font-mono text-[11px] text-muted-foreground">
      {{ (modelValue ?? '').length }} / {{ MAX_LENGTH }}
    </p>
  </div>
</template>
