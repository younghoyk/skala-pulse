<script setup>
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
  <nav class="mb-6 flex gap-1 border-b border-border">
    <RouterLink
      v-for="link in links"
      :key="link.key"
      :to="link.to"
      class="border-b-2 px-3 pb-2.5 text-sm font-medium transition"
      :class="
        active === link.key
          ? 'border-current text-foreground'
          : 'border-transparent text-muted-foreground hover:text-foreground'
      "
      :style="active === link.key ? { color: 'var(--role-instructor)', borderColor: 'var(--role-instructor)' } : {}"
    >
      {{ link.label }}
    </RouterLink>
  </nav>
</template>
