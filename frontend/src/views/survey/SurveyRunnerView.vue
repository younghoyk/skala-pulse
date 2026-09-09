<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useSurveyStore, getNextBlockIndex } from '@/stores/survey'
import { Button } from '@/components/ui/button'
import { Progress } from '@/components/ui/progress'
import ScoreDragBlock from '@/components/survey/ScoreDragBlock.vue'
import CardRankBlock from '@/components/survey/CardRankBlock.vue'
import QuickTagBlock from '@/components/survey/QuickTagBlock.vue'

const props = defineProps({ id: { type: String, required: true } })
const router = useRouter()
const surveyStore = useSurveyStore()

const survey = ref(null)
const loading = ref(true)

const currentIndex = ref(0)
const answers = reactive({})

async function load() {
  loading.value = true
  currentIndex.value = 0
  Object.keys(answers).forEach((key) => delete answers[key])
  try {
    survey.value = await surveyStore.loadSurveyDetail(props.id)
  } catch {
    survey.value = null
  } finally {
    loading.value = false
  }
}

onMounted(load)
// 라우트가 바뀌면(다음 설문으로 이동) 새로 불러온다.
watch(() => props.id, load)

const currentBlock = computed(() => survey.value?.blocks[currentIndex.value] ?? null)
const totalBlocks = computed(() => survey.value?.blocks.length ?? 0)
const progressPercent = computed(() =>
  totalBlocks.value ? ((currentIndex.value + 1) / totalBlocks.value) * 100 : 0,
)
const isLastBlock = computed(() => currentIndex.value === totalBlocks.value - 1)

function setAnswer(blockId, value) {
  answers[blockId] = value
}

function goBack() {
  if (currentIndex.value > 0) currentIndex.value -= 1
}

async function goNext() {
  if (!survey.value) return
  if (isLastBlock.value) {
    await surveyStore.submitSurvey(survey.value.id, { ...answers })
    router.push({ name: 'survey-complete' })
    return
  }
  currentIndex.value = getNextBlockIndex(survey.value, currentIndex.value, answers[currentBlock.value.id])
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
          {{ currentIndex + 1 }} / {{ totalBlocks }}
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
      </div>

      <div class="mt-5 flex justify-between">
        <Button variant="ghost" :disabled="currentIndex === 0" @click="goBack">이전</Button>
        <Button style="background: var(--role-student)" class="text-white hover:opacity-90" @click="goNext">
          {{ isLastBlock ? '제출하기' : '다음' }}
        </Button>
      </div>
    </div>
  </div>
</template>
