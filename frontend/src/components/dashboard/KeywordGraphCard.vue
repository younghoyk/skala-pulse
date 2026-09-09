<script setup>
import { computed } from 'vue'
import { VueFlow } from '@vue-flow/core'
import '@vue-flow/core/dist/style.css'

const props = defineProps({
  graph: { type: Object, required: true }, // { nodes: [{id,label,weight}], edges: [{id,source,target,weight}] }
  positions: { type: Object, required: true }, // { [nodeId]: {x,y} }
})

const style = getComputedStyle(document.documentElement)
const roleColor = style.getPropertyValue('--role-instructor').trim() || '#b8730f'
const roleSoft = style.getPropertyValue('--role-instructor-soft').trim() || '#faecd6'
const inkColor = style.getPropertyValue('--muted-foreground').trim() || '#71717a'

// 언급 빈도 상위 키워드를 그래프 해석 없이 바로 읽히는 목록으로 — 태그가 10종 가까이
// 서로 다 얽혀있으면(동시출현 엣지가 하이볼처럼 됨) 그래프만으론 뭐가 중요한 키워드인지
// 눈에 안 들어와서, 순위 스트립을 먼저 보여준다.
const TOP_KEYWORD_COUNT = 8
const sortedNodes = computed(() => [...props.graph.nodes].sort((a, b) => b.weight - a.weight))
const topKeywords = computed(() => sortedNodes.value.slice(0, TOP_KEYWORD_COUNT))
const maxWeight = computed(() => Math.max(1, ...props.graph.nodes.map((n) => n.weight)))

function keywordFontSize(weight) {
  const ratio = weight / maxWeight.value
  return `${Math.round(13 + ratio * 11)}px`
}

function nodeSize(weight) {
  return Math.round(56 + (weight / maxWeight.value) * 46)
}

const nodes = computed(() =>
  props.graph.nodes.map((n) => {
    const size = nodeSize(n.weight)
    return {
      id: n.id,
      type: 'default',
      position: props.positions[n.id] ?? { x: 0, y: 0 },
      data: { label: n.label },
      draggable: false,
      connectable: false,
      style: {
        width: `${size}px`,
        height: `${size}px`,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        borderRadius: '999px',
        background: roleSoft,
        border: `2px solid ${roleColor}`,
        color: roleColor,
        fontSize: `${Math.min(15, 12 + (n.weight / maxWeight.value) * 4)}px`,
        fontWeight: 700,
        padding: '4px',
        zIndex: 1,
      },
    }
  }),
)

// 엣지를 전부 그리면(태그 10종이면 최대 45개) 노드 사이를 선이 다 뒤덮어서 정작
// 키워드 텍스트가 묻힌다 — 동시출현 가중치 상위 절반만 그려서 "진짜 눈에 띄는 연관성"만
// 남기고, 남은 선도 옅게 해서 노드가 시각적으로 앞서도록 한다.
const edges = computed(() => {
  const sorted = [...props.graph.edges].sort((a, b) => b.weight - a.weight)
  const limit = Math.max(1, Math.ceil(sorted.length / 2))
  const maxEdgeWeight = Math.max(1, ...sorted.map((e) => e.weight))
  return sorted.slice(0, limit).map((e) => ({
    id: e.id,
    source: e.source,
    target: e.target,
    style: {
      stroke: inkColor,
      strokeOpacity: 0.25 + (e.weight / maxEdgeWeight) * 0.35,
      strokeWidth: Math.max(1, e.weight * 0.4),
    },
    animated: false,
  }))
})
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <h3 class="mb-1 text-sm font-semibold text-foreground">키워드 연관 맵</h3>
    <p class="mb-3 text-xs text-muted-foreground">가장 많이 언급된 키워드부터, 함께 언급된 관계는 아래 지도로.</p>

    <div v-if="topKeywords.length" class="mb-4 flex flex-wrap items-center gap-2 rounded-lg bg-muted/40 p-3">
      <span
        v-for="n in topKeywords"
        :key="n.id"
        class="rounded-full px-3 py-1 font-semibold"
        :style="{ background: roleSoft, color: roleColor, fontSize: keywordFontSize(n.weight) }"
      >
        {{ n.label }}
        <span class="ml-1 font-mono text-[10px] font-normal opacity-70">{{ n.weight }}</span>
      </span>
    </div>

    <div style="height: 380px" class="overflow-hidden rounded-lg border border-border">
      <VueFlow :nodes="nodes" :edges="edges" :nodes-draggable="false" :zoom-on-scroll="true" fit-view-on-init>
      </VueFlow>
    </div>
  </div>
</template>
