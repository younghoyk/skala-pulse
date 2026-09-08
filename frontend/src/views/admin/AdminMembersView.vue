<script setup>
import { reactive } from 'vue'
import AdminTopNav from '@/components/admin/AdminTopNav.vue'
import { mockPendingSignups, mockMembers, roleLabels } from '@/data/mockAdminMembers'

const pending = reactive([...mockPendingSignups])
const members = reactive([...mockMembers])

function approve(signup) {
  const idx = pending.findIndex((p) => p.id === signup.id)
  if (idx === -1) return
  pending.splice(idx, 1)
  members.unshift({ id: signup.id, name: signup.name, email: signup.email, role: signup.requestedRole })
}

function reject(id) {
  const idx = pending.findIndex((p) => p.id === id)
  if (idx !== -1) pending.splice(idx, 1)
}
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-2xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">회원 관리</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">가입 승인과 계정별 권한을 관리합니다.</p>

      <AdminTopNav active="members" />

      <section class="mb-8">
        <h2 class="mb-2 flex items-center gap-2 text-sm font-semibold text-foreground">
          가입 승인 대기
          <span class="font-mono text-xs font-normal text-muted-foreground">{{ pending.length }}</span>
        </h2>

        <TransitionGroup tag="ul" name="row" class="flex flex-col gap-2">
          <li
            v-for="signup in pending"
            :key="signup.id"
            class="flex items-center justify-between gap-3 rounded-lg border border-border bg-card px-4 py-3"
          >
            <div>
              <p class="text-sm font-medium text-foreground">{{ signup.name }}</p>
              <p class="text-xs text-muted-foreground">{{ signup.email }}</p>
            </div>
            <div class="flex items-center gap-2">
              <span
                class="rounded-full px-2.5 py-0.5 text-[11px] font-medium"
                style="background: var(--role-admin-soft); color: var(--role-admin)"
              >
                {{ roleLabels[signup.requestedRole] }} 신청
              </span>
              <button
                type="button"
                class="rounded-md px-2.5 py-1 text-xs font-medium text-white hover:opacity-90"
                style="background: var(--role-admin)"
                @click="approve(signup)"
              >
                승인
              </button>
              <button
                type="button"
                class="rounded-md border border-border px-2.5 py-1 text-xs font-medium text-muted-foreground hover:bg-muted"
                @click="reject(signup.id)"
              >
                거절
              </button>
            </div>
          </li>
        </TransitionGroup>
        <p v-if="!pending.length" class="text-sm text-muted-foreground">대기 중인 가입 신청이 없어요.</p>
      </section>

      <section>
        <h2 class="mb-2 flex items-center gap-2 text-sm font-semibold text-foreground">
          전체 회원
          <span class="font-mono text-xs font-normal text-muted-foreground">{{ members.length }}</span>
        </h2>

        <TransitionGroup tag="ul" name="row" class="flex flex-col gap-2">
          <li
            v-for="member in members"
            :key="member.id"
            class="flex items-center justify-between gap-3 rounded-lg border border-border bg-card px-4 py-3"
          >
            <div>
              <p class="text-sm font-medium text-foreground">{{ member.name }}</p>
              <p class="text-xs text-muted-foreground">{{ member.email }}</p>
            </div>
            <select
              v-model="member.role"
              class="rounded-md border border-border bg-card px-2 py-1.5 text-xs text-foreground outline-none"
            >
              <option v-for="(label, key) in roleLabels" :key="key" :value="key">{{ label }}</option>
            </select>
          </li>
        </TransitionGroup>
      </section>
    </div>
  </div>
</template>

<style scoped>
.row-enter-active,
.row-leave-active {
  transition: all 0.25s ease;
}
.row-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}
.row-leave-to {
  opacity: 0;
  transform: translateX(12px);
}
.row-leave-active {
  position: absolute;
  width: 100%;
}
.row-move {
  transition: transform 0.25s ease;
}

@media (prefers-reduced-motion: reduce) {
  .row-enter-active,
  .row-leave-active,
  .row-move {
    transition: none;
  }
}
</style>
