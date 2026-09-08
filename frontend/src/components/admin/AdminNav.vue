<script setup>
defineProps({
  active: { type: String, required: true }, // 'dashboard' | 'feedbacks'
})

const links = [
  { key: 'dashboard', label: '대시보드', to: { name: 'admin-dashboard' } },
  { key: 'feedbacks', label: '피드백 · 키워드', to: { name: 'admin-feedbacks' } },
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
      :style="active === link.key ? { color: 'var(--role-admin)', borderColor: 'var(--role-admin)' } : {}"
    >
      {{ link.label }}
    </RouterLink>
  </nav>
</template>
