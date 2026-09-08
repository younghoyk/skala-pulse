<script setup>
import { reactive } from 'vue'
import draggable from 'vuedraggable'
import { mockKanbanColumns } from '@/data/mockAdminKanban'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'

const state = reactive({
  todo: [...mockKanbanColumns.todo],
  doing: [...mockKanbanColumns.doing],
  done: [...mockKanbanColumns.done],
})

const columns = [
  { key: 'todo', label: '진행 전' },
  { key: 'doing', label: '진행 중' },
  { key: 'done', label: '완료' },
]
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-4xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">개선 과제 칸반 보드</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">여러 과목의 피드백에서 나온 개선 과제를 한 곳에서 관리합니다.</p>

      <AdminTopNav active="kanban" />

      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div v-for="col in columns" :key="col.key" class="rounded-xl border border-border bg-card p-4">
          <div class="mb-3 flex items-center justify-between">
            <h3 class="text-sm font-semibold text-foreground">{{ col.label }}</h3>
            <span class="font-mono text-xs text-muted-foreground">{{ state[col.key].length }}</span>
          </div>

          <draggable
            v-model="state[col.key]"
            group="kanban-tasks"
            item-key="id"
            class="flex min-h-[80px] flex-col gap-2"
            ghost-class="drag-ghost"
            animation="180"
          >
            <template #item="{ element }">
              <div class="cursor-grab rounded-lg border border-border bg-muted/40 p-3 active:cursor-grabbing">
                <p class="text-sm font-medium text-foreground">{{ element.title }}</p>
                <span
                  class="mt-1.5 inline-block rounded-full px-2 py-0.5 text-[11px] font-medium"
                  style="background: var(--role-admin-soft); color: var(--role-admin)"
                >
                  {{ element.courseName }}
                </span>
              </div>
            </template>
          </draggable>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.drag-ghost {
  opacity: 0.4;
  background: var(--role-admin-soft);
}
</style>
