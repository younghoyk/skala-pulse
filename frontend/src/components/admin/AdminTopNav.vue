<script setup>
import TabNav from '@/components/TabNav.vue'

defineProps({
  active: { type: String, required: true }, // 'dashboard' | 'improvements' | 'members' | 'courses'
})

const links = [
  { key: 'dashboard', label: '대시보드', to: { name: 'admin-dashboard' } },
  { key: 'improvements', label: '개선 과제', to: { name: 'admin-improvements' } },
  { key: 'members', label: '회원 관리', to: { name: 'admin-members' } },
  { key: 'courses', label: '과정 관리', to: { name: 'admin-courses' } },
]
</script>

<template>
  <TabNav :links="links" :active="active" accent-var="--role-admin" />
</template>
