<script setup>
import { computed, reactive, ref, watch } from 'vue'
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

const survey = computed(() => surveyStore.getSurveyById(props.id))

const currentIndex = ref(0)
const answers = reactive({})

// 라우트가 바뀌면(다음 설문으로 이동) 진행 상태를 초기화한다.
watch(
  () => props.id,
  () => {
    currentIndex.value = 0
    Object.keys(answers).forEach((key) => delete answers[key])
  },
)

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

function goNext() {
  if (!survey.value) return
  if (isLastBlock.value) {
    surveyStore.submitSurvey(survey.value.id, { ...answers })
    router.push({ name: 'survey-complete' })
    return
  }
  currentIndex.value = getNextBlockIndex(survey.value, currentIndex.value, answers[currentBlock.value.id])
}
</script>

<template>
  <div v-if="!survey" class="flex min-h-svh items-center justify-center px-4 text-center">
    <p class="text-sm text-muted-foreground">
      설문을 찾을 수 없어요. 이미 제출했거나 잘못된 링크일 수 있어요.
    </p>
  </div>

  <div v-else class="flex min-h-svh justify-center bg-muted/40 px-4 py-10">
    <div class="w-full max-w-lg">
      <div class="mb-6">
        <p class="mb-2 text-xs font-medium text-muted-foreground">
          {{ survey.className }} · {{ survey.courseName }}
        </p>
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
