import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { apiFetch } from '@/lib/api'

// 블록 순서 계산을 별도 함수로 분리해둔다 — 지금은 배열 순서대로만 진행하지만,
// 나중에 survey-branching-logic 메모리의 조건부 엣지 평가 로직으로 이 함수만
// 교체하면 SurveyRunnerView 쪽 코드는 건드릴 필요가 없다.
export function getNextBlockIndex(survey, currentIndex /*, answerForCurrentBlock */) {
  return currentIndex + 1
}

export const useSurveyStore = defineStore('survey', () => {
  const surveys = ref([]) // 목록용 — {id, classId, courseName, className, endDate, submitted}

  const pendingSurveys = computed(() => surveys.value.filter((s) => !s.submitted))
  const nextPendingSurveyId = computed(() => pendingSurveys.value[0]?.id ?? null)

  async function loadSurveys() {
    surveys.value = await apiFetch('/api/surveys')
  }

  // 설문 하나의 전체 내용(블록 포함)은 목록과 별도로, 실제로 그 설문을 열 때만 받아온다.
  async function loadSurveyDetail(id) {
    return apiFetch(`/api/surveys/${id}`)
  }

  async function submitSurvey(id, answers) {
    const updated = await apiFetch(`/api/surveys/${id}/responses`, {
      method: 'POST',
      body: { answers },
    })
    const item = surveys.value.find((s) => String(s.id) === String(id))
    if (item) item.submitted = updated.submitted
    return updated
  }

  return { surveys, pendingSurveys, nextPendingSurveyId, loadSurveys, loadSurveyDetail, submitSurvey }
})
