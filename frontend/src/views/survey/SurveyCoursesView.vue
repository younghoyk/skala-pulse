<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSurveyStore } from '@/stores/survey'
import { useAuthStore } from '@/stores/auth'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const surveyStore = useSurveyStore()
const auth = useAuthStore()

onMounted(() => surveyStore.loadSurveys())

const actionableSurveys = computed(() => surveyStore.actionableSurveys)

function formatEndDate(endDate) {
  if (!endDate) return ''
  const [, month, day] = endDate.split('-')
  return `수업 종료일 ${month}.${day}`
}

function openSurvey(survey) {
  router.push({ name: 'survey', params: { id: survey.id } })
}
</script>

<template>
  <div class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-md">
      <PageHeader
        eyebrow="교육생"
        title="제출할 설문"
        :description="`${auth.studentClassName} · 수업이 끝났는데 아직 제출하지 않은 설문만 보여드려요.`"
        accent-var="--role-student"
      />

      <div class="flex flex-col gap-3">
        <button
          v-for="survey in actionableSurveys"
          :key="survey.id"
          type="button"
          class="flex cursor-pointer items-center justify-between rounded-lg border border-border bg-card px-4 py-3.5 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
          @mouseenter="(e) => (e.currentTarget.style.borderColor = 'var(--role-student)')"
          @mouseleave="(e) => (e.currentTarget.style.borderColor = '')"
          @click="openSurvey(survey)"
        >
          <div>
            <p class="text-xs text-muted-foreground">{{ survey.className }}</p>
            <p class="text-sm font-semibold text-foreground">{{ survey.courseName }}</p>
            <p class="mt-0.5 text-[11px] text-muted-foreground">{{ formatEndDate(survey.endDate) }}</p>
          </div>
          <span
            class="shrink-0 rounded-md px-2.5 py-1 text-xs font-medium text-white"
            style="background: var(--role-student)"
          >
            설문 하기
          </span>
        </button>
        <p v-if="!actionableSurveys.length" class="py-6 text-center text-sm text-muted-foreground">
          지금 제출할 설문이 없어요.
        </p>
      </div>
    </div>
  </div>
</template>
