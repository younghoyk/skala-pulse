import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { apiFetch } from '@/lib/api'
import { useAuthStore } from '@/stores/auth'

// survey-branching-logic 메모리의 평가 규칙 그대로: 현재 블록에서 나가는 엣지 중
// 조건 있는 것부터 배열 순서대로(위→아래) 검사해 첫 매치를 taken, 아무것도 안 맞으면
// 조건 없는 기본 엣지로 진행한다. 반환값이 survey.endBlockId면 제출할 차례.
export function resolveNextBlockId(survey, currentBlockId, answer) {
  const outgoing = survey.edges.filter((e) => e.source === currentBlockId)
  const conditioned = outgoing.filter((e) => e.condition)
  const fallback = outgoing.find((e) => !e.condition)
  for (const edge of conditioned) {
    if (matchesCondition(edge.condition, answer)) return edge.target
  }
  return fallback?.target ?? survey.endBlockId
}

function matchesCondition(condition, answer) {
  if (condition.type === 'score-drag') {
    if (typeof answer !== 'number') return false
    const comparators = {
      gte: (a, b) => a >= b,
      lte: (a, b) => a <= b,
      gt: (a, b) => a > b,
      lt: (a, b) => a < b,
      eq: (a, b) => a === b,
    }
    return comparators[condition.comparator]?.(answer, condition.value) ?? false
  }
  if (condition.type === 'card-rank') {
    if (!Array.isArray(answer)) return false
    const position = answer.indexOf(condition.itemId) + 1
    return position >= condition.minPosition && position <= condition.maxPosition
  }
  if (condition.type === 'quick-tag') {
    return Array.isArray(answer?.tagIds) && answer.tagIds.includes(condition.tagId)
  }
  return false
}

export const useSurveyStore = defineStore('survey', () => {
  const surveys = ref([]) // 목록용 — {id, classId, courseName, className, endDate, submitted}

  // 학생이 실제로 봐야 할 설문 = 내 반 && 수업이 끝났는데(종료일이 오늘 이전) && 아직 미제출.
  // 이미 낸 것과 아직 안 끝난 수업은 여기서부터 아예 빠진다 — SurveyCoursesView/
  // SurveyCompleteView 둘 다 이 기준 하나만 본다.
  const actionableSurveys = computed(() => {
    const auth = useAuthStore()
    if (!auth.studentClassName) return []
    const today = new Date().toISOString().slice(0, 10)
    return surveys.value
      .filter((s) => s.className === auth.studentClassName && !s.submitted && s.endDate && s.endDate < today)
      .sort((a, b) => a.endDate.localeCompare(b.endDate))
  })
  const nextActionableSurveyId = computed(() => actionableSurveys.value[0]?.id ?? null)

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
    // 서버의 submitted는 "그 반 전체가 다 냈는지"(집계) 기준이라 내가 방금 낸 것만으론
    // true가 안 될 수 있다 — 내 목록에서는 "내가 이미 냈다"가 기준이어야 하니 무조건 true로.
    const item = surveys.value.find((s) => String(s.id) === String(id))
    if (item) item.submitted = true
    return updated
  }

  return { surveys, actionableSurveys, nextActionableSurveyId, loadSurveys, loadSurveyDetail, submitSurvey }
})
