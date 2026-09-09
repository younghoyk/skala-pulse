<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { apiFetch } from '@/lib/api'
import { Badge } from '@/components/ui/badge'

const router = useRouter()
const auth = useAuthStore()
const hoveredKey = ref(null)

const roles = [
  {
    key: 'student',
    label: '교육생',
    sub: 'Student',
    desc: '수강 중인 수업의 설문에 참여합니다',
    accentVar: '--role-student',
    enabled: true,
  },
  {
    key: 'instructor',
    label: '교수자 / 매니저',
    sub: 'Instructor',
    // 전임/실습은 로그인 시점에 고정되는 게 아니라 반(강의)마다 다른 역할이라
    // (같은 사람이 이 반에선 전임, 저 반에선 실습일 수 있음) 강의 선택 후 화면에서 갈린다.
    desc: '담당 강의를 고르면 그 반에서의 역할(전임/실습)에 맞는 대시보드를 확인합니다',
    accentVar: '--role-instructor',
    enabled: true,
  },
  {
    key: 'admin',
    label: '시스템 관리자',
    sub: 'Super Admin',
    desc: '전체 과정과 설문을 총괄합니다',
    accentVar: '--role-admin',
    enabled: true,
  },
]

const loginError = ref('')

// 교육생은 실제 승인된 회원 중 한 명을 골라 로그인한다(반 정보가 그 회원 걸 따라옴) —
// 원클릭이 아니라 목록에서 선택하는 한 단계가 더 있다.
const showStudentPicker = ref(false)
const students = ref([])
const studentsLoading = ref(false)

async function openStudentPicker() {
  showStudentPicker.value = true
  loginError.value = ''
  studentsLoading.value = true
  try {
    students.value = await apiFetch('/api/auth/demo-students')
  } catch {
    loginError.value = '학생 목록을 불러오지 못했어요. 서버가 켜져 있는지 확인해주세요.'
  } finally {
    studentsLoading.value = false
  }
}

async function loginAsStudent(member) {
  loginError.value = ''
  try {
    await auth.loginAsDemo('student', member.name, member.id)
    router.push({ name: 'survey-courses' })
  } catch {
    loginError.value = '로그인에 실패했어요. 서버가 켜져 있는지 확인해주세요.'
  }
}

async function handleDemoLogin(role) {
  if (!role.enabled) return
  loginError.value = ''

  if (role.key === 'student') {
    openStudentPicker()
    return
  }

  try {
    if (role.key === 'instructor') {
      await auth.loginAsDemo('instructor', '데모 교수자')
      router.push({ name: 'instructor-courses' })
    } else if (role.key === 'admin') {
      await auth.loginAsDemo('admin', '데모 관리자')
      router.push({ name: 'admin-dashboard' })
    }
  } catch {
    loginError.value = '로그인에 실패했어요. 서버가 켜져 있는지 확인해주세요.'
  }
}
</script>

<template>
  <div class="flex min-h-svh items-center justify-center bg-muted/40 px-4">
    <div class="w-full max-w-md">
      <template v-if="!showStudentPicker">
        <p class="mb-1 text-xs font-medium tracking-wide text-muted-foreground">
          SKALA 교육 피드백 &amp; 원인 진단 서비스
        </p>
        <h1 class="mb-1 text-2xl font-bold text-foreground">역할을 선택해주세요</h1>
        <p class="mb-6 text-sm text-muted-foreground">
          원클릭 데모 로그인 — 실제 Slack 로그인은 이후 단계에서 연결됩니다.
        </p>

        <p v-if="loginError" class="mb-4 text-xs font-medium" style="color: var(--danger)">{{ loginError }}</p>

        <div class="flex flex-col gap-3">
          <button
            v-for="role in roles"
            :key="role.key"
            type="button"
            :disabled="!role.enabled"
            class="flex items-center justify-between rounded-lg border bg-card px-4 py-3.5 text-left transition"
            :class="role.enabled ? 'cursor-pointer hover:shadow-sm' : 'cursor-not-allowed border-border opacity-60'"
            :style="role.enabled ? { borderColor: hoveredKey === role.key ? `var(${role.accentVar})` : 'var(--border)' } : {}"
            @mouseenter="hoveredKey = role.key"
            @mouseleave="hoveredKey = null"
            @click="handleDemoLogin(role)"
          >
            <div>
              <div class="flex items-center gap-2">
                <span class="font-semibold text-foreground">{{ role.label }}</span>
                <span class="font-mono text-[11px] text-muted-foreground">{{ role.sub }}</span>
              </div>
              <p class="mt-0.5 text-xs text-muted-foreground">{{ role.desc }}</p>
            </div>
            <Badge v-if="!role.enabled" variant="secondary" class="shrink-0 text-[10px]">
              다음 단계에서 제작
            </Badge>
            <span
              v-else
              class="shrink-0 rounded-md px-2.5 py-1 text-xs font-medium text-white"
              :style="{ background: `var(${role.accentVar})` }"
            >
              데모로 시작
            </span>
          </button>
        </div>

        <RouterLink :to="{ name: 'signup' }" class="mt-4 block text-center text-xs underline text-muted-foreground">
          계정이 없으신가요? 회원가입
        </RouterLink>
      </template>

      <template v-else>
        <button
          type="button"
          class="mb-3 text-xs underline text-muted-foreground"
          @click="showStudentPicker = false"
        >
          ← 역할 선택으로
        </button>
        <h1 class="mb-1 text-2xl font-bold text-foreground">누구로 로그인할까요?</h1>
        <p class="mb-6 text-sm text-muted-foreground">
          회원가입 때 등록한 반으로 그 사람의 설문 목록을 보여드려요.
        </p>

        <p v-if="loginError" class="mb-4 text-xs font-medium" style="color: var(--danger)">{{ loginError }}</p>

        <div v-if="studentsLoading" class="text-sm text-muted-foreground">불러오는 중...</div>
        <div v-else class="flex flex-col gap-2">
          <button
            v-for="member in students"
            :key="member.id"
            type="button"
            class="flex items-center justify-between rounded-lg border border-border bg-card px-4 py-3 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
            @click="loginAsStudent(member)"
          >
            <div>
              <span class="text-sm font-semibold text-foreground">{{ member.name }}</span>
              <span class="ml-1.5 text-xs text-muted-foreground">{{ member.className }} {{ member.serialNumber }}번</span>
            </div>
            <span
              class="shrink-0 rounded-md px-2.5 py-1 text-xs font-medium text-white"
              style="background: var(--role-student)"
            >
              로그인
            </span>
          </button>
          <p v-if="!students.length" class="text-sm text-muted-foreground">승인된 교육생이 없어요.</p>
        </div>
      </template>
    </div>
  </div>
</template>
