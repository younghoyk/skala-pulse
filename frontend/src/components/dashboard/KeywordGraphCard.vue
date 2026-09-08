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

function nodeSize(weight) {
  return Math.round(44 + weight * 3.2)
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
        border: `1.5px solid ${roleColor}`,
        color: roleColor,
        fontSize: `${Math.min(13, 10 + n.weight * 0.15)}px`,
        fontWeight: 600,
        padding: '4px',
      },
    }
  }),
)

const edges = computed(() =>
  props.graph.edges.map((e) => ({
    id: e.id,
    source: e.source,
    target: e.target,
    style: { stroke: inkColor, strokeWidth: Math.max(1, e.weight * 0.4) },
    animated: false,
  })),
)
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <h3 class="mb-1 text-sm font-semibold text-foreground">키워드 연관 맵</h3>
    <p class="mb-3 text-xs text-muted-foreground">노드 크기는 언급 빈도, 선 굵기는 함께 언급된 정도를 나타냅니다.</p>
    <div style="height: 380px" class="overflow-hidden rounded-lg border border-border">
      <VueFlow :nodes="nodes" :edges="edges" :nodes-draggable="false" :zoom-on-scroll="true" fit-view-on-init>
      </VueFlow>
    </div>
  </div>
</template>

<style>
.vue-flow__attribution {
  display: none;
}
</style>
