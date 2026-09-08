<script setup>
import { ref, watch } from 'vue'
import draggable from 'vuedraggable'

const props = defineProps({
  question: { type: String, required: true },
  items: { type: Array, required: true }, // [{ id, label }]
  modelValue: { type: Array, default: null }, // ordered [{ id, label }] or null
})
const emit = defineEmits(['update:modelValue'])

const list = ref(props.modelValue ? [...props.modelValue] : [...props.items])

watch(
  list,
  (val) => {
    emit(
      'update:modelValue',
      val.map((item) => item.id),
    )
  },
  { deep: true, immediate: true },
)
</script>

<template>
  <div>
    <h2 class="mb-1 text-lg font-semibold text-foreground">{{ question }}</h2>
    <p class="mb-6 text-sm text-muted-foreground">
      카드를 드래그해서 위(가장 만족) → 아래(가장 아쉬움) 순서로 배치해주세요.
    </p>

    <draggable v-model="list" item-key="id" class="flex flex-col gap-2.5" ghost-class="drag-ghost" animation="180">
      <template #item="{ element, index }">
        <div
          class="flex cursor-grab items-center gap-3 rounded-lg border border-border bg-card px-4 py-3 shadow-sm active:cursor-grabbing"
        >
          <span
            class="flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white"
            style="background: var(--role-student)"
          >
            {{ index + 1 }}
          </span>
          <span class="font-medium text-foreground">{{ element.label }}</span>
          <svg
            class="ml-auto h-4 w-4 shrink-0 text-muted-foreground"
            viewBox="0 0 20 20"
            fill="currentColor"
            aria-hidden="true"
          >
            <circle cx="7" cy="5" r="1.4" /><circle cx="13" cy="5" r="1.4" />
            <circle cx="7" cy="10" r="1.4" /><circle cx="13" cy="10" r="1.4" />
            <circle cx="7" cy="15" r="1.4" /><circle cx="13" cy="15" r="1.4" />
          </svg>
        </div>
      </template>
    </draggable>
  </div>
</template>

<style scoped>
.drag-ghost {
  opacity: 0.4;
  background: var(--role-student-soft);
}
</style>
