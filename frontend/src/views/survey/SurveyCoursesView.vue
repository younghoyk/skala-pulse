<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSurveyStore } from '@/stores/survey'
import { Badge } from '@/components/ui/badge'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const surveyStore = useSurveyStore()

// 안 한 설문이 위로, 그중에서는 수업 종료일이 임박한(빠른) 반이 위로.
const sortedSurveys = computed(() =>
  [...surveyStore.surveys].sort((a, b) => {
    const submittedDiff = Number(a.submitted) - Number(b.submitted)
    if (submittedDiff !== 0) return submittedDiff
    if (a.submitted) return 0
    return (a.endDate ?? '').localeCompare(b.endDate ?? '')
  }),
)

function formatEndDate(endDate) {
  if (!endDate) return ''
  const [, month, day] = endDate.split('-')
  return `수업 종료일 ${month}.${day}`
}

function openSurvey(survey) {
  if (survey.submitted) return
  router.push({ name: 'survey', params: { id: survey.id } })
}
</script>

<template>
  <div class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-md">
      <PageHeader
        eyebrow="교육생"
        title="수강 중인 강의"
        description="강의 하나당 설문 하나예요. 제출 여부가 바로 표시돼요."
        accent-var="--role-student"
      />

      <div class="flex flex-col gap-3">
        <button
          v-for="survey in sortedSurveys"
          :key="survey.id"
          type="button"
          :disabled="survey.submitted"
          class="flex items-center justify-between rounded-lg border border-border bg-card px-4 py-3.5 text-left shadow-sm transition"
          :class="survey.submitted ? 'cursor-not-allowed opacity-60' : 'cursor-pointer hover:-translate-y-0.5 hover:shadow-md'"
          :style="!survey.submitted ? { '--hover': 'var(--role-student)' } : {}"
          @mouseenter="(e) => !survey.submitted && (e.currentTarget.style.borderColor = 'var(--role-student)')"
          @mouseleave="(e) => (e.currentTarget.style.borderColor = '')"
          @click="openSurvey(survey)"
        >
          <div>
            <p class="text-xs text-muted-foreground">{{ survey.className }}</p>
            <p class="text-sm font-semibold text-foreground">{{ survey.courseName }}</p>
            <p v-if="!survey.submitted && survey.endDate" class="mt-0.5 text-[11px] text-muted-foreground">
              {{ formatEndDate(survey.endDate) }}
            </p>
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
