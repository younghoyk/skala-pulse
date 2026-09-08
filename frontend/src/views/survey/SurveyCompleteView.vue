<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSurveyStore } from '@/stores/survey'
import { mockPreviousFeedbackNotice } from '@/data/mockSurveys'
import { Button } from '@/components/ui/button'

const router = useRouter()
const surveyStore = useSurveyStore()

const nextSurveyId = computed(() => surveyStore.nextPendingSurveyId)

function continueToNext() {
  if (nextSurveyId.value) {
    router.push({ name: 'survey', params: { id: nextSurveyId.value } })
  }
}
</script>

<template>
  <div class="flex min-h-svh items-center justify-center bg-muted/40 px-4">
    <div class="w-full max-w-md text-center">
      <div
        class="checkmark-pop mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full text-white"
        style="background: var(--role-student)"
      >
        <svg class="h-10 w-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
        </svg>
      </div>

      <h1 class="mb-2 text-xl font-bold text-foreground">제출이 완료됐어요</h1>
      <p class="mb-6 text-sm text-muted-foreground">소중한 의견 감사합니다.</p>

      <div
        class="mb-8 rounded-lg border px-4 py-3 text-left text-sm"
        style="background: var(--role-student-soft); border-color: var(--role-student)"
      >
        <p class="mb-0.5 text-xs font-semibold" style="color: var(--role-student)">지난 피드백 반영 결과</p>
        <p class="text-foreground">{{ mockPreviousFeedbackNotice }}</p>
      </div>

      <template v-if="nextSurveyId">
        <p class="mb-3 text-sm text-muted-foreground">아직 제출하지 않은 다른 수업 설문이 남아있어요.</p>
        <Button style="background: var(--role-student)" class="text-white hover:opacity-90" @click="continueToNext">
          다음 설문 이어하기
        </Button>
      </template>
      <template v-else>
        <p class="text-sm font-medium text-foreground">모든 설문을 제출했어요. 수고하셨어요!</p>
      </template>
    </div>
  </div>
</template>

<style scoped>
.checkmark-pop {
  animation: pop 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes pop {
  from {
    transform: scale(0.5);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

@media (prefers-reduced-motion: reduce) {
  .checkmark-pop {
    animation: none;
  }
}
</style>
