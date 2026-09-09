<script setup>
import TabNav from '@/components/TabNav.vue'

defineProps({
  active: { type: String, required: true }, // 'dashboard' | 'keywords' | 'gallery' | 'memo'
})

const links = [
  { key: 'dashboard', label: '대시보드', to: { name: 'instructor-dashboard' } },
  { key: 'keywords', label: '키워드', to: { name: 'instructor-keywords' } },
  { key: 'gallery', label: '피드백 갤러리', to: { name: 'instructor-gallery' } },
  { key: 'memo', label: '액션 메모', to: { name: 'instructor-memo' } },
]
</script>

<template>
  <TabNav :links="links" :active="active" accent-var="--role-instructor" />
</template>
