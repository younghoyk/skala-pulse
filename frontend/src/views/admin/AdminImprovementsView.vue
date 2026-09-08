<script setup>
import { computed, reactive } from 'vue'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'
import { mockPendingImprovements } from '@/data/mockAdminImprovements'

const tasks = reactive([...mockPendingImprovements])

function complete(id) {
  const idx = tasks.findIndex((t) => t.id === id)
  if (idx !== -1) tasks.splice(idx, 1)
}

// 과목을 그룹 헤더로 두고 그 아래에 해당 과제를 나열한다 — 상태 컬럼(진행전/진행중/완료)
// 대신 체크하면 그 항목이 목록에서 바로 사라지는 방식으로 "완료"를 표현한다.
const groupedByCourse = computed(() => {
  const byCourse = new Map()
  for (const task of tasks) {
    if (!byCourse.has(task.courseName)) byCourse.set(task.courseName, [])
    byCourse.get(task.courseName).push(task)
  }
  return [...byCourse.entries()].map(([courseName, items]) => ({ courseName, items }))
})
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-2xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">개선 과제</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">과목별 개선 과제 — 체크하면 완료 처리됩니다.</p>

      <AdminTopNav active="improvements" />

      <div v-if="groupedByCourse.length" class="flex flex-col gap-6">
        <div v-for="group in groupedByCourse" :key="group.courseName">
          <h2 class="mb-2 text-sm font-semibold" style="color: var(--role-admin)">
            {{ group.courseName }}
            <span class="font-mono text-xs font-normal text-muted-foreground">{{ group.items.length }}</span>
          </h2>

          <TransitionGroup tag="ul" name="task" class="flex flex-col gap-2">
            <li
              v-for="task in group.items"
              :key="task.id"
              class="flex items-center gap-3 rounded-lg border border-border bg-card px-4 py-3"
            >
              <button
                type="button"
                class="flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2 transition hover:bg-[var(--role-admin-soft)]"
                style="border-color: var(--role-admin)"
                :aria-label="`${task.title} 완료 처리`"
                @click="complete(task.id)"
              />
              <span class="text-sm text-foreground">{{ task.title }}</span>
            </li>
          </TransitionGroup>
        </div>
      </div>
      <p v-else class="text-sm text-muted-foreground">지금 남아있는 개선 과제가 없어요.</p>
    </div>
  </div>
</template>

<style scoped>
.task-enter-active,
.task-leave-active {
  transition: all 0.25s ease;
}
.task-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}
.task-leave-to {
  opacity: 0;
  transform: translateX(12px);
}
.task-leave-active {
  position: absolute;
  width: 100%;
}
.task-move {
  transition: transform 0.25s ease;
}

@media (prefers-reduced-motion: reduce) {
  .task-enter-active,
  .task-leave-active,
  .task-move {
    transition: none;
  }
}
</style>
