<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { VueFlow } from '@vue-flow/core'
import '@vue-flow/core/dist/style.css'
import { useCoursesStore } from '@/stores/courses'
import {
  mockSurveyTemplatesByCourse,
  blockPalette,
  blockTypeColors,
  blockTypeLabels,
} from '@/data/mockSurveyTemplates'
import AdminCourseNav from '@/components/admin/AdminCourseNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import { Button } from '@/components/ui/button'

const route = useRoute()
const courseId = route.params.courseId
const coursesStore = useCoursesStore()
onMounted(() => coursesStore.loadCourses())
const courseName = computed(
  () => coursesStore.courses.find((c) => String(c.id) === courseId)?.name ?? '',
)

function buildLabel(node) {
  if (node.data.blockType === 'end') return '종료'
  const typeLabel = blockTypeLabels[node.data.blockType]
  const q = node.data.question || '(질문 없음)'
  const preview = q.length > 14 ? `${q.slice(0, 14)}…` : q
  return `${typeLabel}\n${preview}`
}

function styleFor(node) {
  const c = blockTypeColors[node.data.blockType]
  return {
    width: '160px',
    padding: '10px 12px',
    borderRadius: '10px',
    border: `2px solid ${c.border}`,
    background: c.soft,
    color: c.border,
    fontSize: '12px',
    fontWeight: 600,
    textAlign: 'center',
    whiteSpace: 'pre-line',
    cursor: 'pointer',
  }
}

// 항상 빈 캔버스가 아니라 기본 시드 템플릿(종합평가→카드순위→퀵태그→종료)에서 시작한다
// (survey-builder-scope 메모리 원칙). 원본 mock은 건드리지 않게 깊은 복사로 가져온다.
// mockSurveyTemplatesByCourse는 mock 문자열 id('vue' 등)로만 키가 있어서 실제 숫자
// course id는 못 찾는다 — 셋 다 내용이 같은 시드라 아무 키로나 폴백해도 결과는 동일하다
// (빌더 저장을 실제로 만드는 건 이번 범위 밖, 크래시만 막는다).
const template = JSON.parse(
  JSON.stringify(mockSurveyTemplatesByCourse[courseId] ?? mockSurveyTemplatesByCourse.vue),
)

const nodes = ref(
  template.nodes.map((n) => {
    const node = {
      id: n.id,
      position: n.position,
      data: { blockType: n.type, ...n.data },
      deletable: n.deletable !== false,
    }
    node.label = buildLabel(node)
    node.style = styleFor(node)
    return node
  }),
)
const edges = ref(template.edges)

const selectedNodeId = ref(null)
const selectedNode = computed(() => nodes.value.find((n) => n.id === selectedNodeId.value) ?? null)

function onNodeClick({ node }) {
  selectedNodeId.value = node.id
}

function refreshLabel(node) {
  node.label = buildLabel(node)
}

function defaultDataFor(blockType) {
  const now = Date.now()
  switch (blockType) {
    case 'card-rank':
      return { question: '새 질문', items: [{ id: `item-${now}`, label: '항목 1' }] }
    case 'quick-tag':
      return { question: '새 질문', tags: [{ id: `tag-${now}`, label: '태그 1', placeholderHint: '' }] }
    default:
      return { question: '새 질문' }
  }
}

// 이번 단계에서는 조건 없는 엣지 연결까지만 지원한다 — 조건부 분기 UI는 다음 단계.
function onConnect(connection) {
  edges.value.push({
    id: `e-${connection.source}-${connection.target}-${Date.now()}`,
    source: connection.source,
    target: connection.target,
  })
}

function addNode(blockType) {
  const node = {
    id: `${blockType}-${Date.now()}`,
    position: { x: 220 + Math.random() * 400, y: 340 + Math.random() * 120 },
    data: { blockType, ...defaultDataFor(blockType) },
  }
  node.label = buildLabel(node)
  node.style = styleFor(node)
  nodes.value.push(node)
}

function deleteNode(id) {
  const node = nodes.value.find((n) => n.id === id)
  if (!node || node.data.blockType === 'end') return
  nodes.value = nodes.value.filter((n) => n.id !== id)
  edges.value = edges.value.filter((e) => e.source !== id && e.target !== id)
  if (selectedNodeId.value === id) selectedNodeId.value = null
}

function addItem(node) {
  node.data.items.push({ id: `item-${Date.now()}`, label: '새 항목' })
}
function removeItem(node, index) {
  node.data.items.splice(index, 1)
}
function addTag(node) {
  node.data.tags.push({ id: `tag-${Date.now()}`, label: '새 태그', placeholderHint: '' })
}
function removeTag(node, index) {
  node.data.tags.splice(index, 1)
}

const saved = ref(false)
function save() {
  saved.value = true
  setTimeout(() => (saved.value = false), 1500)
}
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-5xl">
      <PageHeader
        eyebrow="시스템 관리자"
        :title="courseName"
        :back-to="{ name: 'admin-dashboard' }"
        back-label="전체 강의 목록으로"
      />

      <AdminCourseNav :course-id="courseId" active="builder" />

      <div class="mb-3 flex flex-wrap items-center gap-2">
        <span class="text-xs text-muted-foreground">블록 추가:</span>
        <button
          v-for="b in blockPalette"
          :key="b.type"
          type="button"
          class="rounded-full border px-3 py-1 text-xs font-medium hover:bg-muted"
          :style="{ borderColor: blockTypeColors[b.type].border, color: blockTypeColors[b.type].border }"
          @click="addNode(b.type)"
        >
          + {{ b.label }}
        </button>
        <div class="ml-auto flex items-center gap-2">
          <span v-if="saved" class="text-xs font-medium" style="color: var(--role-student)">저장됨</span>
          <Button size="sm" style="background: var(--role-admin)" class="text-white hover:opacity-90" @click="save">
            저장
          </Button>
        </div>
      </div>

      <div class="grid grid-cols-1 gap-4 md:grid-cols-[1fr_280px]">
        <div class="overflow-hidden rounded-xl border border-border bg-card" style="height: 480px">
          <VueFlow v-model:nodes="nodes" v-model:edges="edges" fit-view-on-init @connect="onConnect" @node-click="onNodeClick" />
        </div>

        <div class="rounded-xl border border-border bg-card p-4">
          <template v-if="selectedNode">
            <p class="mb-3 text-xs font-semibold" :style="{ color: blockTypeColors[selectedNode.data.blockType].border }">
              {{ blockTypeLabels[selectedNode.data.blockType] }}
            </p>

            <template v-if="selectedNode.data.blockType !== 'end'">
              <label class="mb-1 block text-xs text-muted-foreground">질문</label>
              <textarea
                v-model="selectedNode.data.question"
                rows="2"
                class="mb-3 w-full rounded-md border border-border px-2 py-1.5 text-sm outline-none"
                @input="refreshLabel(selectedNode)"
              />

              <template v-if="selectedNode.data.blockType === 'card-rank'">
                <label class="mb-1 block text-xs text-muted-foreground">항목</label>
                <div v-for="(item, i) in selectedNode.data.items" :key="item.id" class="mb-1.5 flex items-center gap-1.5">
                  <input v-model="item.label" class="flex-1 rounded-md border border-border px-2 py-1 text-xs outline-none" />
                  <button type="button" class="text-xs text-muted-foreground hover:text-foreground" @click="removeItem(selectedNode, i)">
                    ✕
                  </button>
                </div>
                <button type="button" class="text-xs underline" style="color: var(--role-admin)" @click="addItem(selectedNode)">
                  + 항목 추가
                </button>
              </template>

              <template v-else-if="selectedNode.data.blockType === 'quick-tag'">
                <label class="mb-1 block text-xs text-muted-foreground">태그</label>
                <div v-for="(tag, i) in selectedNode.data.tags" :key="tag.id" class="mb-1.5 flex items-center gap-1.5">
                  <input v-model="tag.label" class="flex-1 rounded-md border border-border px-2 py-1 text-xs outline-none" />
                  <button type="button" class="text-xs text-muted-foreground hover:text-foreground" @click="removeTag(selectedNode, i)">
                    ✕
                  </button>
                </div>
                <button type="button" class="text-xs underline" style="color: var(--role-admin)" @click="addTag(selectedNode)">
                  + 태그 추가
                </button>
              </template>

              <Button size="sm" variant="outline" class="mt-4 w-full" @click="deleteNode(selectedNode.id)">이 블록 삭제</Button>
            </template>
            <p v-else class="text-xs text-muted-foreground">설문이 끝나는 지점이에요. 삭제할 수 없어요.</p>
          </template>
          <p v-else class="text-sm text-muted-foreground">캔버스에서 블록을 클릭하면 여기서 내용을 편집할 수 있어요.</p>
        </div>
      </div>

      <p class="mt-3 text-xs text-muted-foreground">
        블록의 연결점을 드래그해서 다른 블록에 이으면 순서가 바뀝니다. 조건부 분기는 다음 단계에서 추가돼요.
      </p>
    </div>
  </div>
</template>
