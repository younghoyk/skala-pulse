// 관리자 "/admin/feedbacks" mock 데이터 — 과목 단위로 묶는다(강사 화면은 반 단위였지만,
// 관리자는 "전체 과정"을 보는 자리라 과목이 기본 단위). Vue.js·알고리즘 기초는
// mockInstructorFeedback.js의 vue-a/algo-a 내용을 그대로 과목 데이터로 재사용해서
// 강사 화면과 다른 이야기를 하지 않게 한다. 데이터베이스 설계는 새로 작성.

import { mockInstructorFeedbackByCourse } from './mockInstructorFeedback'

export const mockAdminFeedbackByCourse = {
  'Vue.js 실전 프로젝트': mockInstructorFeedbackByCourse['vue-a'],
  '알고리즘 기초': mockInstructorFeedbackByCourse['algo-a'],
  '데이터베이스 설계': {
    keywordGraph: {
      nodes: [
        { id: 'assignment', label: '과제', weight: 14 },
        { id: 'difficult', label: '어려움', weight: 11 },
        { id: 'concept', label: '개념', weight: 8 },
        { id: 'practice', label: '실습', weight: 7 },
        { id: 'good', label: '좋아요', weight: 6 },
        { id: 'communication', label: '소통', weight: 5 },
      ],
      edges: [
        { id: 'e1', source: 'assignment', target: 'difficult', weight: 9 },
        { id: 'e2', source: 'assignment', target: 'concept', weight: 4 },
        { id: 'e3', source: 'practice', target: 'concept', weight: 3 },
        { id: 'e4', source: 'communication', target: 'good', weight: 4 },
        { id: 'e5', source: 'concept', target: 'good', weight: 2 },
      ],
    },
    nodePositions: {
      assignment: { x: 320, y: 140 },
      difficult: { x: 500, y: 100 },
      concept: { x: 400, y: 280 },
      practice: { x: 160, y: 320 },
      good: { x: 600, y: 320 },
      communication: { x: 620, y: 180 },
    },
    feedbackGallery: [
      { id: 'd1', tags: ['과제 많음'], summary: '과제가 생각보다 어려웠어요' },
      { id: 'd2', tags: ['과제 많음'], summary: '개념 이해가 안 된 상태로 과제를 풀려니 힘들었어요' },
      { id: 'd3', tags: ['소통 많음'], summary: '막힐 때마다 질문 받아주셔서 좋았어요' },
      { id: 'd4', tags: ['실습 부족'], summary: '실습 예제가 조금 더 있으면 좋겠어요' },
      { id: 'd5', tags: [], summary: '전체적으로 유익한 수업이었어요' },
    ],
  },
}

// "전체" 선택 시 여러 과목의 그래프를 하나로 합친다 — 같은 label의 노드는 weight를 더하고,
// 같은 (label,label) 쌍의 엣지도 weight를 더한다. 병합된 그래프는 원래 좌표를 재사용할 수
// 없으니(과목마다 id가 겹치거나 안 겹치거나 제각각) 원형으로 새로 배치한다.
export function mergeKeywordGraphs(graphs) {
  const nodeWeightByLabel = new Map()
  const edgeWeightByPair = new Map()

  for (const graph of graphs) {
    const idToLabel = {}
    for (const node of graph.nodes) {
      idToLabel[node.id] = node.label
      nodeWeightByLabel.set(node.label, (nodeWeightByLabel.get(node.label) ?? 0) + node.weight)
    }
    for (const edge of graph.edges) {
      const a = idToLabel[edge.source]
      const b = idToLabel[edge.target]
      if (!a || !b || a === b) continue
      const key = [a, b].sort().join('|')
      edgeWeightByPair.set(key, (edgeWeightByPair.get(key) ?? 0) + edge.weight)
    }
  }

  const labels = [...nodeWeightByLabel.keys()]
  const positions = generateCircularPositions(labels)

  return {
    graph: {
      nodes: labels.map((label) => ({ id: label, label, weight: nodeWeightByLabel.get(label) })),
      edges: [...edgeWeightByPair.entries()].map(([key, weight], i) => {
        const [source, target] = key.split('|')
        return { id: `merged-${i}`, source, target, weight }
      }),
    },
    positions,
  }
}

function generateCircularPositions(ids, { centerX = 380, centerY = 220, radius = 200 } = {}) {
  const positions = {}
  ids.forEach((id, i) => {
    const angle = (2 * Math.PI * i) / ids.length
    positions[id] = {
      x: Math.round(centerX + radius * Math.cos(angle)),
      y: Math.round(centerY + radius * Math.sin(angle)),
    }
  })
  return positions
}
