<script setup>
import { computed, onMounted, ref } from 'vue'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useImprovementsStore } from '@/stores/improvements'
import { useCoursesStore } from '@/stores/courses'

const improvementsStore = useImprovementsStore()
const coursesStore = useCoursesStore()

onMounted(async () => {
  await Promise.all([improvementsStore.loadImprovements(), coursesStore.loadCourses()])
  if (coursesStore.courses.length) newTaskCourseId.value = coursesStore.courses[0].id
})

async function complete(id) {
  await improvementsStore.completeImprovement(id)
}

// 과목을 그룹 헤더로 두고 그 아래에 해당 과제를 나열한다 — 상태 컬럼(진행전/진행중/완료)
// 대신 체크하면 그 항목이 목록에서 바로 사라지는 방식으로 "완료"를 표현한다.
const groupedByCourse = computed(() => {
  const byCourse = new Map()
  for (const task of improvementsStore.improvements) {
    if (!byCourse.has(task.courseName)) byCourse.set(task.courseName, [])
    byCourse.get(task.courseName).push(task)
  }
  return [...byCourse.entries()].map(([courseName, items]) => ({ courseName, items }))
})

// 갤러리/키워드에서 "개선 과제로 이관"하는 플로우는 아직 없어서(범위 밖), 과목을 골라
// 직접 입력하는 최소한의 추가 폼만 둔다.
const newTaskCourseId = ref(null)
const newTaskTitle = ref('')

async function addTask() {
  const title = newTaskTitle.value.trim()
  if (!title || !newTaskCourseId.value) return
  await improvementsStore.addImprovement({ courseId: newTaskCourseId.value, title })
  newTaskTitle.value = ''
}
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-2xl">
      <PageHeader
        eyebrow="시스템 관리자"
        title="개선 과제"
        description="과목별 개선 과제 — 체크하면 완료 처리됩니다."
      />

      <AdminTopNav active="improvements" />

      <div class="mb-6 flex flex-wrap items-center gap-2 rounded-lg border border-border bg-card p-3">
        <select
          v-model="newTaskCourseId"
          class="rounded-md border border-border bg-transparent px-2 py-1.5 text-sm outline-none"
        >
          <option v-for="course in coursesStore.courses" :key="course.id" :value="course.id">
            {{ course.name }}
          </option>
        </select>
        <Input
          v-model="newTaskTitle"
          placeholder="새 개선 과제 (예: 실습 예제 자료 추가)"
          class="min-w-48 flex-1"
          @keydown.enter="addTask"
        />
        <Button size="sm" style="background: var(--role-admin)" class="text-white hover:opacity-90" @click="addTask">
          + 추가
        </Button>
      </div>

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
              class="flex items-center gap-3 rounded-lg border border-border bg-card px-4 py-3 shadow-sm"
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
