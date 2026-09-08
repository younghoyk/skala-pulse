import { defineStore } from 'pinia'
import { computed, reactive, ref } from 'vue'
import { mockSurveys } from '@/data/mockSurveys'

// 블록 순서 계산을 별도 함수로 분리해둔다 — 지금은 배열 순서대로만 진행하지만,
// 나중에 survey-branching-logic 메모리의 조건부 엣지 평가 로직으로 이 함수만
// 교체하면 SurveyRunnerView 쪽 코드는 건드릴 필요가 없다.
export function getNextBlockIndex(survey, currentIndex /*, answerForCurrentBlock */) {
  return currentIndex + 1
}

export const useSurveyStore = defineStore('survey', () => {
  const surveys = ref(mockSurveys.map((survey) => ({ ...survey, submitted: false })))
  const responses = reactive({}) // { [surveyId]: { [blockId]: answer } }

  const pendingSurveys = computed(() => surveys.value.filter((s) => !s.submitted))
  const nextPendingSurveyId = computed(() => pendingSurveys.value[0]?.id ?? null)

  function getSurveyById(id) {
    return surveys.value.find((s) => s.id === id) ?? null
  }

  function submitSurvey(id, answers) {
    const survey = getSurveyById(id)
    if (!survey) return
    responses[id] = answers
    survey.submitted = true
  }

  return { surveys, pendingSurveys, nextPendingSurveyId, responses, getSurveyById, submitSurvey }
})
