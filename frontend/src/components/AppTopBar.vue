<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const roleLabels = { student: '교육생', instructor: '교수자', admin: '관리자' }
const roleVar = computed(() => (auth.role ? `--role-${auth.role}` : ''))

function handleLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <header class="flex items-center justify-between border-b border-border bg-card px-4 py-2">
    <div class="h-7 overflow-hidden rounded-md">
      <img src="/skala-logo.png" alt="SKALA" class="h-full w-auto object-cover" />
    </div>

    <div v-if="auth.role" class="flex items-center gap-2.5">
      <span
        class="rounded-full px-2 py-0.5 text-[11px] font-medium"
        :style="{ background: `var(${roleVar}-soft)`, color: `var(${roleVar})` }"
      >
        {{ roleLabels[auth.role] }}
      </span>
      <span class="text-xs text-muted-foreground">{{ auth.displayName }}</span>
      <button
        type="button"
        class="text-xs font-medium text-muted-foreground underline hover:text-foreground"
        @click="handleLogout"
      >
        로그아웃
      </button>
    </div>
  </header>
</template>
