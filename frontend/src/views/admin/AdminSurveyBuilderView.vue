<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { VueFlow } from '@vue-flow/core'
import '@vue-flow/core/dist/style.css'
import { apiFetch } from '@/lib/api'
import { useCoursesStore } from '@/stores/courses'
import { seedTemplate, blockPalette, blockTypeColors, blockTypeLabels } from '@/data/mockSurveyTemplates'
import AdminCourseNav from '@/components/admin/AdminCourseNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import { Button } from '@/components/ui/button'

const route = useRoute()
const courseId = route.params.courseId
const coursesStore = useCoursesStore()
const courseName = computed(
  () => coursesStore.courses.find((c) => String(c.id) === courseId)?.name ?? '',
)

// 조건부 분기를 지원하는 블록 타입 — survey-branching-logic 메모리: 단답형/서술형은
// 자유 서술이라 분기 기준이 될 수 없어서 항상 기본 연결 하나만 가질 수 있다.
const BRANCHABLE_TYPES = ['score-drag', 'card-rank', 'quick-tag']
const COMPARATOR_LABELS = { gte: '≥', lte: '≤', gt: '>', lt: '<', eq: '=' }

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

const nodes = ref([])
const edges = ref([])
const loading = ref(true)

// 조건 없으면 "기본(그 외)" 엣지 — 캔버스 위 표시(굵기/색/라벨)를 조건 유무에 따라 갱신한다.
function conditionSummary(condition, sourceNode) {
  if (!condition) return '기본'
  if (condition.type === 'score-drag') {
    return `점수 ${COMPARATOR_LABELS[condition.comparator] ?? condition.comparator} ${condition.value}`
  }
  if (condition.type === 'card-rank') {
    const item = sourceNode?.data.items?.find((i) => i.id === condition.itemId)
    return `${item?.label ?? '?'} ${condition.minPosition}~${condition.maxPosition}위`
  }
  if (condition.type === 'quick-tag') {
    const tag = sourceNode?.data.tags?.find((t) => t.id === condition.tagId)
    return `태그: ${tag?.label ?? '?'}`
  }
  return ''
}

function refreshEdgeVisual(edge) {
  const sourceNode = nodes.value.find((n) => n.id === edge.source)
  edge.label = conditionSummary(edge.condition, sourceNode)
  edge.style = edge.condition
    ? { stroke: 'var(--role-admin)', strokeWidth: 1.5 }
    : { stroke: 'var(--muted-foreground)', strokeWidth: 1 }
  edge.labelStyle = { fontSize: '10px', fill: edge.condition ? 'var(--role-admin)' : 'var(--muted-foreground)' }
}

// 저장된 그래프(백엔드 SurveyTemplateGraphDto: {id,type,position,data})를 VueFlow가
// 쓰는 모양으로 변환 — end 노드만 삭제 불가로 고정한다(deletable은 서버에 저장 안 함,
// type으로부터 매번 다시 계산).
function hydrate(graph) {
  nodes.value = graph.nodes.map((n) => {
    const node = {
      id: n.id,
      position: n.position,
      data: { blockType: n.type, ...n.data },
      deletable: n.type !== 'end',
    }
    node.label = buildLabel(node)
    node.style = styleFor(node)
    return node
  })
  edges.value = graph.edges.map((e) => ({
    id: e.id,
    source: e.source,
    target: e.target,
    condition: e.condition ?? null,
  }))
  edges.value.forEach(refreshEdgeVisual)
}

// 항상 빈 캔버스가 아니라 기본 시드 템플릿에서 시작한다(survey-builder-scope 메모리
// 원칙) — 이 과정에 저장된 커스텀 템플릿이 있으면 그걸, 없으면(null) 로컬 시드를 쓴다.
async function load() {
  loading.value = true
  await coursesStore.loadCourses()
  let graph = null
  try {
    graph = await apiFetch(`/api/courses/${courseId}/survey-template`)
  } catch {
    graph = null
  }
  hydrate(graph ?? seedTemplate())
  loading.value = false
}
onMounted(load)

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

function outgoingEdgesOf(nodeId) {
  return edges.value.filter((e) => e.source === nodeId)
}

// 새로 드래그로 연결한 엣지가 그 블록의 첫 연결이면 기본(무조건) 엣지, 이미 하나 있으면
// 조건부 엣지 — 빈 조건 스텁을 만들어서 옆 패널에서 채우게 한다.
function defaultConditionFor(sourceNode) {
  const blockType = sourceNode.data.blockType
  if (blockType === 'score-drag') return { type: 'score-drag', comparator: 'gte', value: 3 }
  if (blockType === 'card-rank') {
    const firstItem = sourceNode.data.items?.[0]
    return { type: 'card-rank', itemId: firstItem?.id ?? null, minPosition: 1, maxPosition: 1 }
  }
  if (blockType === 'quick-tag') {
    const firstTag = sourceNode.data.tags?.[0]
    return { type: 'quick-tag', tagId: firstTag?.id ?? null }
  }
  return null
}

// target에서 시작해 기존 엣지를 따라가서 source에 도달할 수 있으면, 이 연결은 사이클을
// 만든다 — 루프 금지(survey-branching-logic 메모리)를 캔버스에서 바로 막는다.
function wouldCreateCycle(sourceId, targetId) {
  const seen = new Set()
  const stack = [targetId]
  while (stack.length) {
    const id = stack.pop()
    if (id === sourceId) return true
    if (seen.has(id)) continue
    seen.add(id)
    for (const e of edges.value.filter((edge) => edge.source === id)) stack.push(e.target)
  }
  return false
}

const connectError = ref('')

function onConnect(connection) {
  connectError.value = ''
  const sourceNode = nodes.value.find((n) => n.id === connection.source)
  if (!sourceNode) return

  const existing = outgoingEdgesOf(connection.source)
  const isBranchable = BRANCHABLE_TYPES.includes(sourceNode.data.blockType)
  if (!isBranchable && existing.length >= 1) {
    connectError.value = '단답형/서술형 블록은 분기(여러 연결)를 지원하지 않아요 — 기존 연결을 지우고 다시 이어주세요.'
    return
  }
  if (wouldCreateCycle(connection.source, connection.target)) {
    connectError.value = '이 연결은 순환 구조를 만들어서 추가할 수 없어요.'
    return
  }

  const isFirst = existing.length === 0
  const edge = {
    id: `e-${connection.source}-${connection.target}-${Date.now()}`,
    source: connection.source,
    target: connection.target,
    condition: isFirst ? null : defaultConditionFor(sourceNode),
  }
  refreshEdgeVisual(edge)
  edges.value.push(edge)
}

function removeEdge(edgeId) {
  edges.value = edges.value.filter((e) => e.id !== edgeId)
}

function targetLabel(targetId) {
  const node = nodes.value.find((n) => n.id === targetId)
  if (!node) return '?'
  if (node.data.blockType === 'end') return '종료(제출)'
  return node.data.question || blockTypeLabels[node.data.blockType]
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

// nodes/edges를 백엔드 SurveyTemplateGraphDto 모양({id,type,position,data}/
// {id,source,target,condition})으로 되돌린다 — blockType은 data 안이 아니라 node.type으로,
// label/style/labelStyle(VueFlow 렌더용 파생값)은 뺀다.
function toStoredGraph() {
  return {
    nodes: nodes.value.map((n) => {
      const { blockType, ...data } = n.data
      return { id: n.id, type: blockType, position: n.position, data }
    }),
    edges: edges.value.map((e) => ({ id: e.id, source: e.source, target: e.target, condition: e.condition ?? null })),
  }
}

const saveState = ref('idle') // 'idle' | 'saving' | 'saved' | 'error'
const saveErrorMessage = ref('')

async function save() {
  saveState.value = 'saving'
  saveErrorMessage.value = ''
  try {
    await apiFetch(`/api/courses/${courseId}/survey-template`, { method: 'PUT', body: toStoredGraph() })
    saveState.value = 'saved'
    setTimeout(() => {
      if (saveState.value === 'saved') saveState.value = 'idle'
    }, 1500)
  } catch (err) {
    saveState.value = 'error'
    saveErrorMessage.value = err.message ?? '저장에 실패했어요.'
  }
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

      <p v-if="loading" class="py-10 text-center text-sm text-muted-foreground">불러오는 중...</p>
      <template v-else>
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
            <span v-if="saveState === 'saved'" class="text-xs font-medium" style="color: var(--role-student)">저장됨</span>
            <span v-else-if="saveState === 'error'" class="text-xs font-medium" style="color: var(--danger)">
              {{ saveErrorMessage }}
            </span>
            <Button
              size="sm"
              :disabled="saveState === 'saving'"
              style="background: var(--role-admin)"
              class="text-white hover:opacity-90"
              @click="save"
            >
              {{ saveState === 'saving' ? '저장 중...' : '저장' }}
            </Button>
          </div>
        </div>
        <p v-if="connectError" class="mb-2 text-xs font-medium" style="color: var(--danger)">{{ connectError }}</p>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-[1fr_280px]">
          <div class="overflow-hidden rounded-xl border border-border bg-card" style="height: 480px">
            <VueFlow v-model:nodes="nodes" v-model:edges="edges" fit-view-on-init @connect="onConnect" @node-click="onNodeClick" />
          </div>

          <div class="rounded-xl border border-border bg-card p-4" style="max-height: 480px; overflow-y: auto">
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

                <div class="mt-4 border-t border-border pt-3">
                  <label class="mb-1.5 block text-xs font-medium text-muted-foreground">다음으로 연결</label>

                  <div
                    v-for="edge in outgoingEdgesOf(selectedNode.id)"
                    :key="edge.id"
                    class="mb-2 rounded-md border border-border p-2"
                  >
                    <div class="mb-1 flex items-center justify-between">
                      <span
                        class="text-[11px] font-semibold"
                        :style="{ color: edge.condition ? 'var(--role-admin)' : 'var(--muted-foreground)' }"
                      >
                        {{ edge.condition ? '조건부' : '기본(그 외)' }}
                      </span>
                      <button type="button" class="text-xs text-muted-foreground hover:text-foreground" @click="removeEdge(edge.id)">
                        ✕
                      </button>
                    </div>

                    <div v-if="edge.condition?.type === 'score-drag'" class="flex flex-wrap items-center gap-1.5 text-xs">
                      <span>점수</span>
                      <select
                        v-model="edge.condition.comparator"
                        class="rounded border border-border px-1 py-0.5 text-xs"
                        @change="refreshEdgeVisual(edge)"
                      >
                        <option value="gte">≥</option>
                        <option value="lte">≤</option>
                        <option value="gt">&gt;</option>
                        <option value="lt">&lt;</option>
                        <option value="eq">=</option>
                      </select>
                      <input
                        v-model.number="edge.condition.value"
                        type="number"
                        step="0.1"
                        class="w-16 rounded border border-border px-1 py-0.5 text-xs"
                        @input="refreshEdgeVisual(edge)"
                      />
                    </div>

                    <div v-else-if="edge.condition?.type === 'card-rank'" class="flex flex-wrap items-center gap-1.5 text-xs">
                      <select
                        v-model="edge.condition.itemId"
                        class="rounded border border-border px-1 py-0.5 text-xs"
                        @change="refreshEdgeVisual(edge)"
                      >
                        <option v-for="item in selectedNode.data.items" :key="item.id" :value="item.id">{{ item.label }}</option>
                      </select>
                      <span>순위</span>
                      <input
                        v-model.number="edge.condition.minPosition"
                        type="number"
                        min="1"
                        :max="selectedNode.data.items.length"
                        class="w-12 rounded border border-border px-1 py-0.5 text-xs"
                        @input="refreshEdgeVisual(edge)"
                      />
                      <span>~</span>
                      <input
                        v-model.number="edge.condition.maxPosition"
                        type="number"
                        min="1"
                        :max="selectedNode.data.items.length"
                        class="w-12 rounded border border-border px-1 py-0.5 text-xs"
                        @input="refreshEdgeVisual(edge)"
                      />
                      <span>위</span>
                    </div>

                    <select
                      v-else-if="edge.condition?.type === 'quick-tag'"
                      v-model="edge.condition.tagId"
                      class="w-full rounded border border-border px-1 py-0.5 text-xs"
                      @change="refreshEdgeVisual(edge)"
                    >
                      <option v-for="tag in selectedNode.data.tags" :key="tag.id" :value="tag.id">{{ tag.label }}</option>
                    </select>

                    <p class="mt-1 text-[11px] text-muted-foreground">→ {{ targetLabel(edge.target) }}</p>
                  </div>

                  <p v-if="!outgoingEdgesOf(selectedNode.id).length" class="text-[11px] text-muted-foreground">
                    캔버스에서 이 블록을 다른 블록으로 드래그해 연결하세요.
                  </p>
                </div>

                <Button size="sm" variant="outline" class="mt-4 w-full" @click="deleteNode(selectedNode.id)">이 블록 삭제</Button>
              </template>
              <p v-else class="text-xs text-muted-foreground">설문이 끝나는 지점이에요. 삭제할 수 없어요.</p>
            </template>
            <p v-else class="text-sm text-muted-foreground">캔버스에서 블록을 클릭하면 여기서 내용을 편집할 수 있어요.</p>
          </div>
        </div>

        <p class="mt-3 text-xs text-muted-foreground">
          블록의 연결점을 드래그해서 다른 블록에 이으면 새 연결이 생겨요 — 첫 연결은 기본, 그다음부터는 조건부 분기예요
          (단답형/서술형 제외). 모든 블록은 조건 없는 기본 연결이 하나 있어야 저장할 수 있어요.
        </p>
      </template>
    </div>
  </div>
</template>
