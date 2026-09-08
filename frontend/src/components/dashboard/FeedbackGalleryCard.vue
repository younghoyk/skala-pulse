<script setup>
defineProps({
  items: { type: Array, required: true }, // [{ id, tags: [label], summary, courseName? }]
})
</script>

<template>
  <div class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <h3 class="mb-1 text-sm font-semibold text-foreground">피드백 갤러리</h3>
    <p class="mb-4 text-xs text-muted-foreground">태그와 한 줄 요약만 — 익명입니다.</p>

    <ul class="grid grid-cols-1 gap-3 sm:grid-cols-2">
      <li v-for="item in items" :key="item.id" class="rounded-lg border border-border p-3">
        <p v-if="item.courseName" class="mb-1.5 text-[11px] font-medium text-muted-foreground">{{ item.courseName }}</p>
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
</template>
