<script setup>
import { reactive, ref } from 'vue'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'
import { mockCourseManagement, instructorOptions, labInstructorFor } from '@/data/mockAdminCourseManagement'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

const courses = reactive(JSON.parse(JSON.stringify(mockCourseManagement)))

const showNewCourseForm = ref(false)
const newCourseName = ref('')

function addCourse() {
  const name = newCourseName.value.trim()
  if (!name) return
  courses.push({ id: `course-${Date.now()}`, name, leadInstructorName: instructorOptions[0] ?? '', classes: [] })
  newCourseName.value = ''
  showNewCourseForm.value = false
}

const newClassForms = reactive({}) // courseId -> { open, name, capacity, format, labInstructorName }

function blankClassForm() {
  return {
    open: true,
    name: '',
    capacity: 20,
    format: 'zoom',
    labInstructorName: instructorOptions[0] ?? '',
    startDate: '',
    endDate: '',
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
    format: form.format,
    labInstructorName: form.format === 'direct' ? null : form.labInstructorName,
    startDate: form.startDate,
    endDate: form.endDate,
    submission: { submitted: 0, total: capacity },
  })
  newClassForms[course.id] = { ...blankClassForm(), open: false }
}

function deleteClass(course, classId) {
  const idx = course.classes.findIndex((c) => c.id === classId)
  if (idx === -1) return
  if (!confirm(`${course.name} ${course.classes[idx].name}을(를) 삭제할까요?`)) return
  course.classes.splice(idx, 1)
}

function deleteCourse(courseId) {
  const idx = courses.findIndex((c) => c.id === courseId)
  if (idx === -1) return
  if (!confirm(`${courses[idx].name} 과정을 삭제할까요? 소속된 반도 함께 삭제됩니다.`)) return
  courses.splice(idx, 1)
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
      <p class="mb-5 text-xs text-muted-foreground">
        전임교수는 과목 단위(모든 반 공통), 실습교수는 반 단위입니다. 직강 반은 전임교수가 실습도 겸해요.
      </p>

      <AdminTopNav active="courses" />

      <div v-if="showNewCourseForm" class="mb-6 flex items-center gap-2 rounded-lg border border-border bg-card p-3">
        <Input v-model="newCourseName" placeholder="새 과정 이름 (예: 프론트엔드 심화)" class="flex-1" @keydown.enter="addCourse" />
        <Button size="sm" style="background: var(--role-admin)" class="text-white hover:opacity-90" @click="addCourse">
          추가
        </Button>
      </div>

      <div class="flex flex-col gap-8">
        <section v-for="course in courses" :key="course.id">
          <div class="mb-2 flex items-center gap-3">
            <h2 class="text-sm font-semibold text-foreground">{{ course.name }}</h2>
            <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
              전임교수
              <select
                v-model="course.leadInstructorName"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }}</option>
              </select>
            </label>
            <button
              type="button"
              class="ml-auto rounded-md border px-2.5 py-1 text-xs font-medium hover:opacity-90"
              style="border-color: var(--danger); color: var(--danger)"
              @click="deleteCourse(course.id)"
            >
              과정 삭제
            </button>
          </div>

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
                수업 방식
                <select
                  v-model="cls.format"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                >
                  <option value="zoom">줌</option>
                  <option value="direct">직강</option>
                </select>
              </label>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                실습교수
                <select
                  v-if="cls.format === 'zoom'"
                  v-model="cls.labInstructorName"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                >
                  <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }}</option>
                </select>
                <span v-else class="text-foreground">{{ labInstructorFor(course, cls) }} (전임과 동일)</span>
              </label>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                수업 기간
                <input
                  v-model="cls.startDate"
                  type="date"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                />
                ~
                <input
                  v-model="cls.endDate"
                  type="date"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                />
              </label>

              <span class="ml-auto shrink-0 font-mono text-xs text-muted-foreground">
                제출 {{ cls.submission.submitted }}명 / {{ cls.submission.total }}명
              </span>

              <button
                type="button"
                class="shrink-0 rounded-md border px-2 py-1 text-xs font-medium hover:opacity-90"
                style="border-color: var(--danger); color: var(--danger)"
                @click="deleteClass(course, cls.id)"
              >
                삭제
              </button>
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
                v-model="newClassForms[course.id].format"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option value="zoom">줌</option>
                <option value="direct">직강</option>
              </select>
              <select
                v-if="newClassForms[course.id].format === 'zoom'"
                v-model="newClassForms[course.id].labInstructorName"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option v-for="name in instructorOptions" :key="name" :value="name">{{ name }} (실습)</option>
              </select>
              <span v-else class="text-xs text-muted-foreground">실습교수: 전임과 동일</span>
              <input
                v-model="newClassForms[course.id].startDate"
                type="date"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              />
              ~
              <input
                v-model="newClassForms[course.id].endDate"
                type="date"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              />
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
