<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSurveyStore } from '@/stores/survey'
import { Badge } from '@/components/ui/badge'

const router = useRouter()
const surveyStore = useSurveyStore()

// 안 한 설문이 위로 — 제출 여부만 기준으로 정렬(제출 안 함 먼저).
const sortedSurveys = computed(() =>
  [...surveyStore.surveys].sort((a, b) => Number(a.submitted) - Number(b.submitted)),
)

function openSurvey(survey) {
  if (survey.submitted) return
  router.push({ name: 'survey', params: { id: survey.id } })
}
</script>

<template>
  <div class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-md">
      <p class="mb-1 text-xs font-medium text-muted-foreground">교육생</p>
      <h1 class="mb-1 text-xl font-bold text-foreground">수강 중인 강의</h1>
      <p class="mb-6 text-sm text-muted-foreground">강의 하나당 설문 하나예요. 제출 여부가 바로 표시돼요.</p>

      <div class="flex flex-col gap-3">
        <button
          v-for="survey in sortedSurveys"
          :key="survey.id"
          type="button"
          :disabled="survey.submitted"
          class="flex items-center justify-between rounded-lg border border-border bg-card px-4 py-3.5 text-left transition"
          :class="survey.submitted ? 'cursor-not-allowed opacity-60' : 'cursor-pointer hover:shadow-sm'"
          :style="!survey.submitted ? { '--hover': 'var(--role-student)' } : {}"
          @mouseenter="(e) => !survey.submitted && (e.currentTarget.style.borderColor = 'var(--role-student)')"
          @mouseleave="(e) => (e.currentTarget.style.borderColor = '')"
          @click="openSurvey(survey)"
        >
          <div>
            <p class="text-xs text-muted-foreground">{{ survey.className }}</p>
            <p class="text-sm font-semibold text-foreground">{{ survey.courseName }}</p>
          </div>
          <Badge
            v-if="survey.submitted"
            variant="secondary"
            class="shrink-0 text-[10px]"
          >
            제출 완료
          </Badge>
          <span
            v-else
            class="shrink-0 rounded-md px-2.5 py-1 text-xs font-medium text-white"
            style="background: var(--role-student)"
          >
            설문 하기
          </span>
        </button>
      </div>
    </div>
  </div>
</template>
