<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  items: { type: Array, required: true }, // [{ id, tags: [label], summary, courseName? }]
})

// 태그 칩 하나 클릭하면 그 태그가 달린 피드백만 — 다시 클릭하면 해제. 언급 빈도순으로
// 나열해서 가장 흔한 불만/칭찬부터 훑어볼 수 있게 한다.
const activeTag = ref(null)

const tagCounts = computed(() => {
  const counts = new Map()
  for (const item of props.items) {
    for (const tag of item.tags) counts.set(tag, (counts.get(tag) ?? 0) + 1)
  }
  return [...counts.entries()].sort((a, b) => b[1] - a[1])
})

function toggleTag(tag) {
  activeTag.value = activeTag.value === tag ? null : tag
}

const filteredItems = computed(() =>
  activeTag.value ? props.items.filter((item) => item.tags.includes(activeTag.value)) : props.items,
)

// 반(courseName — 이 prop 자리엔 실제로 반 이름이 들어온다) 기준으로 묶어서 보여준다 —
// AdminImprovementsView.vue의 "그룹 헤더 + 그 아래 목록" 패턴과 같은 방식. 카드 131개를
// 그냥 쏟아놓는 대신 한 번에 소화 가능한 단위로 나눈다.
const groupedByClass = computed(() => {
  const byClass = new Map()
  for (const item of filteredItems.value) {
    const key = item.courseName ?? '기타'
    if (!byClass.has(key)) byClass.set(key, [])
    byClass.get(key).push(item)
  }
  return [...byClass.entries()]
    .map(([className, items]) => ({ className, items }))
    .sort((a, b) => a.className.localeCompare(b.className, 'ko'))
})
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <h3 class="mb-1 text-sm font-semibold text-foreground">피드백 갤러리</h3>
    <p class="mb-4 text-xs text-muted-foreground">태그와 한 줄 요약만 — 익명입니다.</p>

    <div v-if="tagCounts.length" class="mb-4 flex flex-wrap gap-1.5">
      <button
        type="button"
        class="rounded-full border px-2.5 py-1 text-[11px] font-medium transition"
        :class="!activeTag ? 'border-transparent text-white' : 'border-border bg-card text-foreground hover:bg-muted'"
        :style="!activeTag ? { background: 'var(--role-instructor)' } : {}"
        @click="activeTag = null"
      >
        전체 {{ items.length }}
      </button>
      <button
        v-for="[tag, count] in tagCounts"
        :key="tag"
        type="button"
        class="rounded-full border px-2.5 py-1 text-[11px] font-medium transition"
        :class="
          activeTag === tag ? 'border-transparent text-white' : 'border-border bg-card text-foreground hover:bg-muted'
        "
        :style="activeTag === tag ? { background: 'var(--role-instructor)' } : {}"
        @click="toggleTag(tag)"
      >
        {{ tag }} {{ count }}
      </button>
    </div>

    <div v-if="groupedByClass.length" class="flex flex-col gap-5">
      <div v-for="group in groupedByClass" :key="group.className">
        <h4 class="mb-2 text-xs font-semibold" style="color: var(--role-instructor)">
          {{ group.className }}
          <span class="font-mono text-[11px] font-normal text-muted-foreground">{{ group.items.length }}</span>
        </h4>
        <ul class="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <li v-for="item in group.items" :key="item.id" class="rounded-lg border border-border p-3">
            <div v-if="item.tags.length" class="mb-1.5 flex flex-wrap gap-1.5">
              <span
                v-for="tag in item.tags"
                :key="tag"
                class="rounded-full px-2.5 py-0.5 text-[11px] font-medium"
                style="background: var(--role-instructor-soft); color: var(--role-instructor)"
              >
                {{ tag }}
              </span>
            </div>
            <p class="text-sm text-foreground">{{ item.summary }}</p>
          </li>
        </ul>
      </div>
    </div>
    <p v-else class="text-sm text-muted-foreground">해당하는 피드백이 없어요.</p>
  </div>
</template>
