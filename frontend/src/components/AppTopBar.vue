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
  <header class="sticky top-0 z-10 flex items-center justify-between bg-card/90 px-5 py-2.5 shadow-sm backdrop-blur-sm">
    <img src="/skala-logo.png" alt="SKALA" class="h-6 w-auto" />

    <div v-if="auth.role" class="flex items-center gap-3">
      <span
        class="flex items-center gap-1.5 rounded-full px-2.5 py-1 text-[11px] font-semibold"
        :style="{ background: `var(${roleVar}-soft)`, color: `var(${roleVar})` }"
      >
        <span class="h-1.5 w-1.5 rounded-full" :style="{ background: `var(${roleVar})` }" />
        {{ roleLabels[auth.role] }}
      </span>
      <span class="text-xs text-muted-foreground">{{ auth.displayName }}</span>
      <button
        type="button"
        class="text-xs font-medium text-muted-foreground transition hover:text-foreground"
        @click="handleLogout"
      >
        로그아웃
      </button>
    </div>
  </header>
</template>
