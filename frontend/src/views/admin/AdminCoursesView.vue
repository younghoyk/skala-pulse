<script setup>
import { reactive, ref } from 'vue'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'
import { mockCourseManagement, instructorOptions } from '@/data/mockAdminCourseManagement'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

const courses = reactive(JSON.parse(JSON.stringify(mockCourseManagement)))

const showNewCourseForm = ref(false)
const newCourseName = ref('')

function addCourse() {
  const name = newCourseName.value.trim()
  if (!name) return
  courses.push({ id: `course-${Date.now()}`, name, classes: [] })
  newCourseName.value = ''
  showNewCourseForm.value = false
}

const newClassForms = reactive({}) // courseId -> { open, name, capacity, leadInstructorName, labInstructorName }

function blankClassForm() {
  return {
    open: true,
    name: '',
    capacity: 20,
    leadInstructorName: instructorOptions[0] ?? '',
    labInstructorName: instructorOptions[1] ?? instructorOptions[0] ?? '',
  }
}

function openClassForm(courseId) {
  newClassForms[courseId] = blankClassForm()
}

function addClass(course) {
  const form = newClassForms[course.id]
  if (!form?.name.trim()) return
  const capacity = Number(form.capacity) || 0
  course.classes.push({
    id: `${course.id}-${Date.now()}`,
    name: form.name.trim(),
    capacity,
    leadInstructorName: form.leadInstructorName,
    labInstructorName: form.labInstructorName,
    submission: { submitted: 0, total: capacity },
  })
  newClassForms[course.id] = { ...blankClassForm(), open: false }
}
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-2xl">
      <div class="mb-1 flex items-start justify-between gap-3">
        <div>
          <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
          <h1 class="text-xl font-bold text-foreground">과정 관리</h1>
        </div>
        <Button size="sm" variant="outline" @click="showNewCourseForm = !showNewCourseForm">+ 새 과정 추가</Button>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">반 총원 설정, 담당 강사 배정, 반별 제출 현황(카운트만)을 관리합니다.</p>

      <AdminTopNav active="courses" />

      <div v-if="showNewCourseForm" class="mb-6 flex items-center gap-2 rounded-lg border border-border bg-card p-3">
        <Input v-model="newCourseName" placeholder="새 과정 이름 (예: 프론트엔드 심화)" class="flex-1" @keydown.enter="addCourse" />
        <Button size="sm" style="background: var(--role-admin)" class="text-white hover:opacity-90" @click="addCourse">
          추가
        </Button>
      </div>

      <div class="flex flex-col gap-8">
        <section v-for="course in courses" :key="course.id">
          <h2 class="mb-2 text-sm font-semibold text-foreground">{{ course.name }}</h2>

          <div class="flex flex-col gap-2">
            <div
              v-for="cls in course.classes"
              :key="cls.id"
              class="flex flex-wrap items-center gap-3 rounded-lg border border-border bg-card px-4 py-3"
            >
              <span class="w-12 shrink-0 text-sm font-medium text-foreground">{{ cls.name }}</span>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                총원
                <input
                  v-model.number="cls.capacity"
                  type="number"
                  min="0"
                  class="w-16 rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                />
              </label>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                전임교수
                <select
                  v-model="cls.leadInstructorName"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                >
                  <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }}</option>
                </select>
              </label>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                실습교수
                <select
                  v-model="cls.labInstructorName"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                >
                  <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }}</option>
                </select>
              </label>

              <span class="ml-auto shrink-0 font-mono text-xs text-muted-foreground">
                제출 {{ cls.submission.submitted }}명 / {{ cls.submission.total }}명
              </span>
            </div>

            <div v-if="newClassForms[course.id]?.open" class="flex flex-wrap items-center gap-2 rounded-lg border border-dashed border-border p-3">
              <Input v-model="newClassForms[course.id].name" placeholder="반 이름 (예: C반)" class="w-28" />
              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                총원
                <input
                  v-model.number="newClassForms[course.id].capacity"
                  type="number"
                  min="0"
                  class="w-16 rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                />
              </label>
              <select
                v-model="newClassForms[course.id].leadInstructorName"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }} (전임)</option>
              </select>
              <select
                v-model="newClassForms[course.id].labInstructorName"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }} (실습)</option>
              </select>
              <Button size="sm" style="background: var(--role-admin)" class="text-white hover:opacity-90" @click="addClass(course)">
                추가
              </Button>
            </div>
            <button
              v-else
              type="button"
              class="self-start text-xs font-medium underline"
              style="color: var(--role-admin)"
              @click="openClassForm(course.id)"
            >
              + 반 추가
            </button>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
