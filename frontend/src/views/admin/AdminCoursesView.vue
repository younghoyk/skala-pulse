<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ChevronRight } from '@lucide/vue'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useCoursesStore } from '@/stores/courses'
import { useMembersStore } from '@/stores/members'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

const coursesStore = useCoursesStore()
const membersStore = useMembersStore()

const searchKeyword = ref('')

// 종료일 오름차순 하나로 "이미 끝난 과정 먼저(가장 오래전에 끝난 것부터), 그다음 아직
// 안 끝난 과정(가장 가까운 일정부터)"이 자연히 함께 성립한다 — AdminDashboardView.vue와
// 같은 기준. 정렬/필터된 배열이라도 원소는 스토어의 같은 반응형 객체라 인라인 편집은
// 그대로 동작한다.
const courses = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  return coursesStore.courses
    .filter((course) => !keyword || course.name.toLowerCase().includes(keyword))
    .sort((a, b) => {
      const dateA = a.classes[0]?.endDate ?? '9999-12-31'
      const dateB = b.classes[0]?.endDate ?? '9999-12-31'
      return dateA.localeCompare(dateB)
    })
})

// 실습/전임교수 선택지는 실제 회원 목록에서 뽑는다 — mock 시절엔 이름 문자열이었지만
// 이제 진짜 회원 id로 바인딩한다.
const instructorOptions = computed(() => membersStore.members.filter((m) => m.role === 'instructor'))

// 과정 34개 x 반 5개를 한 화면에 전부 펼쳐두면 너무 길어져서, 기본은 접어두고
// 펼친 과정만 반 목록을 보여준다.
const expandedCourses = ref(new Set())
function toggleCourse(courseId) {
  if (expandedCourses.value.has(courseId)) {
    expandedCourses.value.delete(courseId)
  } else {
    expandedCourses.value.add(courseId)
  }
}

onMounted(async () => {
  if (!membersStore.members.length) await membersStore.loadAll()
  await coursesStore.loadCourses()
})

const showNewCourseForm = ref(false)
const newCourseName = ref('')

async function addCourse() {
  const name = newCourseName.value.trim()
  if (!name || !instructorOptions.value.length) return
  await coursesStore.addCourse({ name, leadInstructorId: instructorOptions.value[0].id })
  newCourseName.value = ''
  showNewCourseForm.value = false
}

async function deleteCourse(courseId, courseName) {
  if (!confirm(`${courseName} 과정을 삭제할까요? 소속된 반도 함께 삭제됩니다.`)) return
  await coursesStore.deleteCourse(courseId)
}

async function onLeadInstructorChange(course) {
  await coursesStore.updateCourse(course.id, { leadInstructorId: course.leadInstructorId })
}

const newClassForms = reactive({}) // courseId -> { open, name, format, labInstructorId, startDate, endDate }

function blankClassForm() {
  return {
    open: true,
    name: '',
    format: 'zoom',
    labInstructorId: instructorOptions.value[0]?.id ?? null,
    startDate: '',
    endDate: '',
  }
}

function openClassForm(courseId) {
  newClassForms[courseId] = blankClassForm()
}

async function addClass(course) {
  const form = newClassForms[course.id]
  if (!form?.name.trim()) return
  await coursesStore.addClass(course.id, {
    name: form.name.trim(),
    format: form.format,
    labInstructorId: form.format === 'direct' ? null : form.labInstructorId,
    startDate: form.startDate || null,
    endDate: form.endDate || null,
  })
  newClassForms[course.id] = { ...blankClassForm(), open: false }
}

async function deleteClass(courseId, cls) {
  if (!confirm(`${cls.name}을(를) 삭제할까요?`)) return
  await coursesStore.deleteClass(courseId, cls.id)
}

function onClassFieldChange(cls, field) {
  coursesStore.updateClass(cls.id, { [field]: cls[field] })
}

function onClassFormatChange(cls) {
  const fields = { format: cls.format }
  coursesStore.updateClass(cls.id, fields)
}
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-2xl">
      <PageHeader
        eyebrow="시스템 관리자"
        title="과정 관리"
        description="전임교수는 과목 단위(모든 반 공통), 실습교수는 반 단위입니다. 직강 반은 전임교수가 실습도 겸해요."
      >
        <template #actions>
          <Button size="sm" variant="outline" @click="showNewCourseForm = !showNewCourseForm">+ 새 과정 추가</Button>
        </template>
      </PageHeader>

      <AdminTopNav active="courses" />

      <Input v-model="searchKeyword" placeholder="과정 이름으로 검색" class="mb-4" />

      <div v-if="showNewCourseForm" class="mb-6 flex items-center gap-2 rounded-lg border border-border bg-card p-3">
        <Input v-model="newCourseName" placeholder="새 과정 이름 (예: 프론트엔드 심화)" class="flex-1" @keydown.enter="addCourse" />
        <Button size="sm" style="background: var(--role-admin)" class="text-white hover:opacity-90" @click="addCourse">
          추가
        </Button>
      </div>

      <div class="flex flex-col gap-8">
        <p v-if="!courses.length" class="py-6 text-center text-sm text-muted-foreground">검색 결과가 없어요.</p>
        <section v-for="course in courses" :key="course.id">
          <div class="mb-2 flex items-center gap-3">
            <button
              type="button"
              class="flex shrink-0 items-center justify-center rounded-md p-0.5 text-muted-foreground transition hover:bg-muted"
              :aria-label="expandedCourses.has(course.id) ? '접기' : '펼치기'"
              @click="toggleCourse(course.id)"
            >
              <ChevronRight
                class="h-4 w-4 transition-transform"
                :style="{ transform: expandedCourses.has(course.id) ? 'rotate(90deg)' : 'rotate(0deg)' }"
              />
            </button>
            <button
              type="button"
              class="text-sm font-semibold text-foreground hover:underline"
              @click="toggleCourse(course.id)"
            >
              {{ course.name }}
            </button>
            <span class="text-xs text-muted-foreground">{{ course.classes.length }}개 반</span>
            <label class="ml-auto flex items-center gap-1.5 text-xs text-muted-foreground">
              전임교수
              <select
                v-model="course.leadInstructorId"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                @change="onLeadInstructorChange(course)"
              >
                <option v-for="instructor in instructorOptions" :key="instructor.id" :value="instructor.id">
                  {{ instructor.name }}
                </option>
              </select>
            </label>
            <button
              type="button"
              class="rounded-md border px-2.5 py-1 text-xs font-medium hover:opacity-90"
              style="border-color: var(--danger); color: var(--danger)"
              @click="deleteCourse(course.id, course.name)"
            >
              과정 삭제
            </button>
          </div>

          <div v-if="expandedCourses.has(course.id)" class="flex flex-col gap-2">
            <div
              v-for="cls in course.classes"
              :key="cls.id"
              class="flex flex-wrap items-center gap-3 rounded-lg border border-border bg-card px-4 py-3 shadow-sm"
            >
              <span class="w-12 shrink-0 text-sm font-medium text-foreground">{{ cls.name }}</span>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                수업 방식
                <select
                  v-model="cls.format"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                  @change="onClassFormatChange(cls)"
                >
                  <option value="zoom">줌</option>
                  <option value="direct">직강</option>
                </select>
              </label>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                실습교수
                <select
                  v-if="cls.format === 'zoom'"
                  v-model="cls.labInstructorId"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                  @change="onClassFieldChange(cls, 'labInstructorId')"
                >
                  <option v-for="instructor in instructorOptions" :key="instructor.id" :value="instructor.id">
                    {{ instructor.name }}
                  </option>
                </select>
                <span v-else class="text-foreground">{{ cls.labInstructorName }} (전임과 동일)</span>
              </label>

              <label class="flex items-center gap-1.5 text-xs text-muted-foreground">
                수업 기간
                <input
                  v-model="cls.startDate"
                  type="date"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                  @change="onClassFieldChange(cls, 'startDate')"
                />
                ~
                <input
                  v-model="cls.endDate"
                  type="date"
                  class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
                  @change="onClassFieldChange(cls, 'endDate')"
                />
              </label>

              <span class="ml-auto shrink-0 font-mono text-xs text-muted-foreground">
                제출 {{ cls.submission.submitted }}명 / {{ cls.submission.total }}명
              </span>

              <button
                type="button"
                class="shrink-0 rounded-md border px-2 py-1 text-xs font-medium hover:opacity-90"
                style="border-color: var(--danger); color: var(--danger)"
                @click="deleteClass(course.id, cls)"
              >
                삭제
              </button>
            </div>

            <div v-if="newClassForms[course.id]?.open" class="flex flex-wrap items-center gap-2 rounded-lg border border-dashed border-border p-3">
              <Input v-model="newClassForms[course.id].name" placeholder="반 이름 (예: C반)" class="w-28" />
              <select
                v-model="newClassForms[course.id].format"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option value="zoom">줌</option>
                <option value="direct">직강</option>
              </select>
              <select
                v-if="newClassForms[course.id].format === 'zoom'"
                v-model="newClassForms[course.id].labInstructorId"
                class="rounded-md border border-border bg-card px-2 py-1 text-xs text-foreground outline-none"
              >
                <option v-for="instructor in instructorOptions" :key="instructor.id" :value="instructor.id">
                  {{ instructor.name }} (실습)
                </option>
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
            <Button
              v-else
              size="sm"
              variant="outline"
              class="self-start"
              @click="openClassForm(course.id)"
            >
              + 반 추가
            </Button>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
