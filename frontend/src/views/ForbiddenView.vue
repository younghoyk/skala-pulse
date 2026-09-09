<script setup>
import { Button } from '@/components/ui/button'
</script>

<template>
  <div class="flex min-h-svh items-center justify-center bg-muted/40 px-4">
    <div class="w-full max-w-sm text-center">
      <p class="mb-2 text-5xl font-bold" style="color: var(--danger)">403</p>
      <h1 class="mb-2 text-xl font-bold text-foreground">접근 권한이 없어요</h1>
      <p class="mb-8 text-sm text-muted-foreground">
        이 페이지는 현재 계정의 역할로 볼 수 없어요. 다른 역할로 로그인해주세요.
      </p>

      <RouterLink :to="{ name: 'login' }">
        <Button style="background: var(--role-admin)" class="text-white hover:opacity-90">로그인 화면으로</Button>
      </RouterLink>
    </div>
  </div>
</template>
