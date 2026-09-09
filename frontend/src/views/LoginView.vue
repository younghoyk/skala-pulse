<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
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

function handleDemoLogin(role) {
  if (!role.enabled) return

  if (role.key === 'student') {
    auth.loginAsDemo('student', '데모 교육생')
    router.push({ name: 'survey-courses' })
  } else if (role.key === 'instructor') {
    auth.loginAsDemo('instructor', '데모 교수자')
    router.push({ name: 'instructor-courses' })
  } else if (role.key === 'admin') {
    auth.loginAsDemo('admin', '데모 관리자')
    router.push({ name: 'admin-dashboard' })
  }
}
</script>

<template>
  <div class="flex min-h-svh items-center justify-center bg-muted/40 px-4">
    <div class="w-full max-w-md">
      <p class="mb-1 text-xs font-medium tracking-wide text-muted-foreground">
        SKALA 교육 피드백 &amp; 원인 진단 서비스
      </p>
      <h1 class="mb-1 text-2xl font-bold text-foreground">역할을 선택해주세요</h1>
      <p class="mb-6 text-sm text-muted-foreground">
        원클릭 데모 로그인 — 실제 Slack 로그인은 이후 단계에서 연결됩니다.
      </p>

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
    </div>
  </div>
</template>
