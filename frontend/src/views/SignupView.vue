<script setup>
import { reactive, ref } from 'vue'
import { useMembersStore } from '@/stores/members'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

const membersStore = useMembersStore()

const CLASS_OPTIONS = ['1반', '2반', '3반', '4반', '5반']

const form = reactive({ name: '', email: '', requestedRole: 'student', className: CLASS_OPTIONS[0], serialNumber: '' })
const submitted = ref(false)
const submitError = ref('')

async function submit() {
  if (!form.name.trim() || !form.email.trim()) return
  if (form.requestedRole === 'student' && !form.serialNumber.trim()) return
  submitError.value = ''
  try {
    await membersStore.requestSignup({
      name: form.name.trim(),
      email: form.email.trim(),
      requestedRole: form.requestedRole,
      className: form.requestedRole === 'student' ? form.className : null,
      serialNumber: form.requestedRole === 'student' ? form.serialNumber.trim() : null,
    })
    submitted.value = true
  } catch {
    submitError.value = '가입 신청에 실패했어요. 잠시 후 다시 시도해주세요.'
  }
}
</script>

<template>
  <div class="flex min-h-svh items-center justify-center bg-muted/40 px-4">
    <div class="w-full max-w-md">
      <template v-if="!submitted">
        <p class="mb-1 text-xs font-medium tracking-wide text-muted-foreground">
          SKALA 교육 피드백 &amp; 원인 진단 서비스
        </p>
        <h1 class="mb-1 text-2xl font-bold text-foreground">회원가입</h1>
        <p class="mb-6 text-sm text-muted-foreground">
          신청 후 관리자 승인이 완료되면 로그인할 수 있어요.
        </p>

        <p v-if="submitError" class="mb-3 text-xs font-medium" style="color: var(--danger)">{{ submitError }}</p>

        <form class="flex flex-col gap-3" @submit.prevent="submit">
          <div>
            <label class="mb-1 block text-xs font-medium text-muted-foreground">이름</label>
            <Input v-model="form.name" placeholder="홍길동" required />
          </div>
          <div>
            <label class="mb-1 block text-xs font-medium text-muted-foreground">이메일</label>
            <Input v-model="form.email" type="email" placeholder="name@skala.io" required />
          </div>
          <div>
            <label class="mb-1 block text-xs font-medium text-muted-foreground">희망 역할</label>
            <select
              v-model="form.requestedRole"
              class="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-foreground outline-none"
            >
              <option value="student">교육생</option>
              <option value="instructor">교수자</option>
            </select>
          </div>

          <template v-if="form.requestedRole === 'student'">
            <div>
              <label class="mb-1 block text-xs font-medium text-muted-foreground">반</label>
              <select
                v-model="form.className"
                class="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-foreground outline-none"
              >
                <option v-for="c in CLASS_OPTIONS" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>
            <div>
              <label class="mb-1 block text-xs font-medium text-muted-foreground">일련번호</label>
              <Input v-model="form.serialNumber" placeholder="예: 07" required />
            </div>
          </template>

          <Button type="submit" class="mt-2 text-white hover:opacity-90" style="background: var(--role-admin)">
            가입 신청하기
          </Button>
        </form>

        <RouterLink :to="{ name: 'login' }" class="mt-4 block text-center text-xs underline text-muted-foreground">
          로그인 화면으로
        </RouterLink>
      </template>

      <template v-else>
        <div class="text-center">
          <div
            class="checkmark-pop mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full text-white"
            style="background: var(--role-admin)"
          >
            <svg class="h-10 w-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h1 class="mb-2 text-xl font-bold text-foreground">가입 신청이 완료됐어요</h1>
          <p class="mb-8 text-sm text-muted-foreground">관리자 승인 후 로그인할 수 있어요.</p>

          <RouterLink :to="{ name: 'login' }">
            <Button style="background: var(--role-admin)" class="text-white hover:opacity-90">로그인 화면으로</Button>
          </RouterLink>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.checkmark-pop {
  animation: pop 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes pop {
  from {
    transform: scale(0.5);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

@media (prefers-reduced-motion: reduce) {
  .checkmark-pop {
    animation: none;
  }
}
</style>
