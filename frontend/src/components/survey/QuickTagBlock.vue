<script setup>
import { computed, nextTick, ref, watch } from 'vue'

const props = defineProps({
  question: { type: String, required: true },
  tags: { type: Array, required: true }, // [{ id, label, placeholderHint }]
  summaryMaxLength: { type: Number, default: 100 },
  modelValue: { type: Object, default: null }, // { tagIds: [], summary: '' }
})
const emit = defineEmits(['update:modelValue'])

const selectedTagIds = ref(props.modelValue?.tagIds ? [...props.modelValue.tagIds] : [])
const summary = ref(props.modelValue?.summary ?? '')
const textareaRef = ref(null)

async function toggleTag(tagId) {
  const idx = selectedTagIds.value.indexOf(tagId)
  if (idx === -1) {
    selectedTagIds.value.push(tagId)
    await nextTick()
    textareaRef.value?.focus() // Tab 없이도 바로 이어서 입력할 수 있게, 선택 직후 요약창으로 포커스 이동
  } else {
    selectedTagIds.value.splice(idx, 1)
  }
}

// 여러 태그를 고르면 각 태그의 힌트를 이어붙인다. placeholder에는 "예: "를 붙여서 보여주고,
// Tab 자동완성으로 채울 때는 그 접두어 없이 실제 입력값만 넣는다.
const combinedHint = computed(() =>
  props.tags
    .filter((tag) => selectedTagIds.value.includes(tag.id))
    .map((tag) => tag.placeholderHint)
    .filter(Boolean)
    .join(' · '),
)
const dynamicPlaceholder = computed(() => (combinedHint.value ? `예: ${combinedHint.value}` : '한 줄로 남겨주세요 (선택 입력)'))

// 화면 폭에 걸려 잘리는 대신, 입력창이 내용에 맞춰 아래로 자라도록 높이를 다시 계산한다.
function autoResize() {
  const el = textareaRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}
// 실제 입력값(summary)뿐 아니라, 태그 선택으로 placeholder만 길어질 때도 다시 재보한다 —
// 값이 비어있어도 placeholder가 길면 미리 여러 줄로 자리를 잡아둔다.
watch([summary, dynamicPlaceholder], () => nextTick(autoResize), { immediate: true })

// 포커스 가능한 요소들 사이에서 Tab/Shift+Tab으로 이동한다 — textarea는 기본 Tab이
// 탭 문자를 입력해버리므로, input이었을 때와 같은 "다음 요소로 이동" 동작을 직접 구현.
function moveFocus(current, direction) {
  const focusable = Array.from(
    document.querySelectorAll(
      'a[href], button:not([disabled]), input:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])',
    ),
  ).filter((el) => el.offsetParent !== null)
  const idx = focusable.indexOf(current)
  if (idx === -1) return
  focusable[idx + direction]?.focus()
}

// 입력창이 비어있을 때 Tab을 누르면, 직접 타이핑하는 대신 placeholder 힌트를 그대로 채워준다.
function onSummaryKeydown(e) {
  if (e.key !== 'Tab') return
  e.preventDefault()
  if (!e.shiftKey && !summary.value && combinedHint.value) {
    summary.value = combinedHint.value.slice(0, props.summaryMaxLength)
    return
  }
  moveFocus(e.target, e.shiftKey ? -1 : 1)
}

watch(
  [selectedTagIds, summary],
  () => {
    emit('update:modelValue', { tagIds: [...selectedTagIds.value], summary: summary.value })
  },
  { deep: true, immediate: true },
)
</script>

<template>
  <div>
    <h2 class="mb-1 text-lg font-semibold text-foreground">{{ question }}</h2>
    <p class="mb-4 text-sm text-muted-foreground">해당하는 태그를 골라주세요 (복수 선택 가능, 선택 입력).</p>

    <div class="mb-6 flex flex-wrap gap-2">
      <button
        v-for="tag in tags"
        :key="tag.id"
        type="button"
        class="rounded-full border px-3.5 py-1.5 text-sm font-medium transition"
        :class="
          selectedTagIds.includes(tag.id)
            ? 'border-transparent text-white'
            : 'border-border bg-card text-foreground hover:bg-muted'
        "
        :style="selectedTagIds.includes(tag.id) ? { background: 'var(--role-student)' } : {}"
        @click="toggleTag(tag.id)"
      >
        {{ tag.label }}
      </button>
    </div>

    <label class="mb-1.5 block text-xs font-medium text-muted-foreground">
      한 줄 요약 (선택)
      <span v-if="combinedHint && !summary" class="font-normal text-muted-foreground/70">— Tab을 누르면 예시가 그대로 채워져요</span>
    </label>
    <textarea
      ref="textareaRef"
      v-model="summary"
      rows="1"
      :maxlength="summaryMaxLength"
      :placeholder="dynamicPlaceholder"
      class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 block w-full resize-none overflow-hidden rounded-lg border bg-transparent px-2.5 py-1.5 text-base leading-normal break-words whitespace-pre-wrap outline-none placeholder:text-muted-foreground focus-visible:ring-3 md:text-sm"
      @keydown="onSummaryKeydown"
      @input="autoResize"
    />
    <p class="mt-1 text-right font-mono text-[11px] text-muted-foreground">
      {{ summary.length }} / {{ summaryMaxLength }}
    </p>
  </div>
</template>
