<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useSurveyStore, resolveNextBlockId } from '@/stores/survey'
import { Button } from '@/components/ui/button'
import { Progress } from '@/components/ui/progress'
import ScoreDragBlock from '@/components/survey/ScoreDragBlock.vue'
import CardRankBlock from '@/components/survey/CardRankBlock.vue'
import QuickTagBlock from '@/components/survey/QuickTagBlock.vue'
import ShortAnswerBlock from '@/components/survey/ShortAnswerBlock.vue'
import LongAnswerBlock from '@/components/survey/LongAnswerBlock.vue'

const props = defineProps({ id: { type: String, required: true } })
const router = useRouter()
const surveyStore = useSurveyStore()

const survey = ref(null)
const loading = ref(true)

// 분기 때문에 실제 경로 길이가 미리 정해지지 않아서(사람마다 다름) "다음 인덱스"가 아니라
// "다음 블록 id"로 진행한다 — 방문 순서를 스택처럼 쌓아두고 뒤로가기는 그냥 pop.
const visited = ref([])
const answers = reactive({})

async function load() {
  loading.value = true
  Object.keys(answers).forEach((key) => delete answers[key])
  try {
    survey.value = await surveyStore.loadSurveyDetail(props.id)
    visited.value = survey.value ? [survey.value.startBlockId] : []
  } catch {
    survey.value = null
    visited.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)
// 라우트가 바뀌면(다음 설문으로 이동) 새로 불러온다.
watch(() => props.id, load)

const currentBlockId = computed(() => visited.value[visited.value.length - 1] ?? null)
const currentBlock = computed(() => survey.value?.blocks.find((b) => b.id === currentBlockId.value) ?? null)
const totalBlocks = computed(() => survey.value?.blocks.length ?? 0)
// 분기로 실제 경로가 전체 블록 수보다 짧아질 수 있어서(건너뛴 블록만큼) 100%를 못 채우고
// 제출될 수 있다 — "지금까지 방문한 스텝 수 / 전체 블록 수"로의 근사, 폼 서비스에서 흔한 방식.
const progressPercent = computed(() => (totalBlocks.value ? (visited.value.length / totalBlocks.value) * 100 : 0))
// 지금 블록에서 지금 답변으로 "다음"을 누르면 바로 제출인지 미리 계산 — 버튼 라벨용.
const willSubmitNext = computed(() => {
  if (!survey.value || !currentBlock.value) return false
  const next = resolveNextBlockId(survey.value, currentBlock.value.id, answers[currentBlock.value.id])
  return next === survey.value.endBlockId
})

function setAnswer(blockId, value) {
  answers[blockId] = value
}

function goBack() {
  if (visited.value.length > 1) visited.value.pop()
}

async function goNext() {
  if (!survey.value || !currentBlock.value) return
  const nextId = resolveNextBlockId(survey.value, currentBlock.value.id, answers[currentBlock.value.id])
  if (nextId === survey.value.endBlockId) {
    await surveyStore.submitSurvey(survey.value.id, { ...answers })
    router.push({ name: 'survey-complete' })
    return
  }
  visited.value.push(nextId)
}
</script>

<template>
  <div v-if="loading" class="flex min-h-svh items-center justify-center px-4 text-center">
    <p class="text-sm text-muted-foreground">불러오는 중...</p>
  </div>

  <div v-else-if="!survey" class="flex min-h-svh items-center justify-center px-4 text-center">
    <p class="text-sm text-muted-foreground">
      설문을 찾을 수 없어요. 이미 제출했거나 잘못된 링크일 수 있어요.
    </p>
  </div>

  <div v-else class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-lg">
      <div class="mb-6">
        <div class="mb-2 flex items-center justify-between gap-3">
          <p class="text-xs font-medium text-muted-foreground">
            {{ survey.className }} · {{ survey.courseName }}
          </p>
          <RouterLink :to="{ name: 'survey-courses' }" class="text-xs underline text-muted-foreground shrink-0">
            강의 선택으로
          </RouterLink>
        </div>
        <Progress :model-value="progressPercent" class="mb-1.5" />
        <p class="text-right font-mono text-[11px] text-muted-foreground">
          {{ visited.length }} / {{ totalBlocks }}
        </p>
      </div>

      <div class="rounded-xl border border-border bg-card p-6 shadow-sm">
        <ScoreDragBlock
          v-if="currentBlock.type === 'score-drag'"
          :key="currentBlock.id"
          :question="currentBlock.question"
          :min="currentBlock.min"
          :max="currentBlock.max"
          :model-value="answers[currentBlock.id] ?? null"
          @update:model-value="(v) => setAnswer(currentBlock.id, v)"
        />
        <CardRankBlock
          v-else-if="currentBlock.type === 'card-rank'"
          :key="currentBlock.id"
          :question="currentBlock.question"
          :items="currentBlock.items"
          :model-value="answers[currentBlock.id] ?? null"
          @update:model-value="(v) => setAnswer(currentBlock.id, v)"
        />
        <QuickTagBlock
          v-else-if="currentBlock.type === 'quick-tag'"
          :key="currentBlock.id"
          :question="currentBlock.question"
          :tags="currentBlock.tags"
          :summary-max-length="currentBlock.summaryMaxLength"
          :model-value="answers[currentBlock.id] ?? null"
          @update:model-value="(v) => setAnswer(currentBlock.id, v)"
        />
        <ShortAnswerBlock
          v-else-if="currentBlock.type === 'short-answer'"
          :key="currentBlock.id"
          :question="currentBlock.question"
          :model-value="answers[currentBlock.id] ?? ''"
          @update:model-value="(v) => setAnswer(currentBlock.id, v)"
        />
        <LongAnswerBlock
          v-else-if="currentBlock.type === 'long-answer'"
          :key="currentBlock.id"
          :question="currentBlock.question"
          :model-value="answers[currentBlock.id] ?? ''"
          @update:model-value="(v) => setAnswer(currentBlock.id, v)"
        />
      </div>

      <div class="mt-5 flex justify-between">
        <Button variant="ghost" :disabled="visited.length === 1" @click="goBack">이전</Button>
        <Button style="background: var(--role-student)" class="text-white hover:opacity-90" @click="goNext">
          {{ willSubmitNext ? '제출하기' : '다음' }}
        </Button>
      </div>
    </div>
  </div>
</template>
