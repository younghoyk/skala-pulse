<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ email: '', password: '' })
const loginError = ref('')
const loading = ref(false)

// 역할을 고르는 화면이 아니다 — 이메일+비밀번호로 로그인하면 그 계정의 실제 역할에
// 맞는 화면으로 넘어간다(역할별 목적지는 기존 원클릭 데모 로그인 때와 동일).
async function handleLogin() {
  if (!form.email.trim() || !form.password) return
  loginError.value = ''
  loading.value = true
  try {
    const role = await auth.login(form.email.trim(), form.password)
    if (role === 'student') router.push({ name: 'survey-courses' })
    else if (role === 'instructor') router.push({ name: 'instructor-courses' })
    else router.push({ name: 'admin-dashboard' })
  } catch (err) {
    loginError.value = err.message ?? '로그인에 실패했어요. 서버가 켜져 있는지 확인해주세요.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex min-h-svh items-center justify-center bg-muted/40 px-4">
    <div class="w-full max-w-sm">
      <p class="mb-1 text-xs font-medium tracking-wide text-muted-foreground">
        SKALA 교육 피드백 &amp; 원인 진단 서비스
      </p>
      <h1 class="mb-1 text-2xl font-bold text-foreground">로그인</h1>
      <p class="mb-6 text-sm text-muted-foreground">이메일과 비밀번호로 로그인해주세요.</p>

      <p v-if="loginError" class="mb-4 text-xs font-medium" style="color: var(--danger)">{{ loginError }}</p>

      <form class="flex flex-col gap-3" @submit.prevent="handleLogin">
        <div>
          <label class="mb-1 block text-xs font-medium text-muted-foreground">이메일</label>
          <Input v-model="form.email" type="email" placeholder="name@skala.io" autocomplete="username" required />
        </div>
        <div>
          <label class="mb-1 block text-xs font-medium text-muted-foreground">비밀번호</label>
          <Input v-model="form.password" type="password" autocomplete="current-password" required />
        </div>

        <Button
          type="submit"
          :disabled="loading"
          class="mt-2 text-white hover:opacity-90"
          style="background: var(--role-admin)"
        >
          {{ loading ? '로그인 중...' : '로그인' }}
        </Button>
      </form>

      <RouterLink :to="{ name: 'signup' }" class="mt-4 block text-center text-xs underline text-muted-foreground">
        계정이 없으신가요? 회원가입
      </RouterLink>
    </div>
  </div>
</template>
