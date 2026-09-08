<script setup>
import { ref } from 'vue'
import { Button } from '@/components/ui/button'

const memo = ref('')
const saved = ref(false)

function save() {
  saved.value = true
}

function onEdit() {
  saved.value = false
}
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <h3 class="mb-1 text-sm font-semibold text-foreground">수업 개선 액션 메모</h3>
    <p class="mb-3 text-xs text-muted-foreground">다음 수업에서 시도해볼 개선 사항을 적어두세요.</p>

    <textarea
      v-model="memo"
      rows="4"
      placeholder="예: 다음 주부터 실습 세션을 30분 늘려본다"
      class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 w-full resize-none rounded-lg border bg-transparent px-2.5 py-2 text-sm outline-none placeholder:text-muted-foreground focus-visible:ring-3"
      @input="onEdit"
    />

    <div class="mt-3 flex items-center justify-between">
      <p v-if="saved" class="text-xs font-medium" style="color: var(--role-student)">저장됐어요</p>
      <p v-else class="text-xs text-muted-foreground">아직 저장되지 않았어요</p>
      <Button
        size="sm"
        :disabled="!memo"
        style="background: var(--role-instructor)"
        class="text-white hover:opacity-90"
        @click="save"
      >
        저장
      </Button>
    </div>
  </div>
</template>
