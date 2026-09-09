<script setup>
defineProps({
  links: { type: Array, required: true }, // [{ key, label, to }]
  active: { type: String, required: true },
  accentVar: { type: String, default: '--role-admin' },
})
</script>

<template>
  <nav class="mb-7 flex gap-1 border-b border-border">
    <RouterLink
      v-for="link in links"
      :key="link.key"
      :to="link.to"
      class="border-b-2 px-3 pb-3 text-sm font-medium transition"
      :class="
        active === link.key
          ? 'border-current text-foreground'
          : 'border-transparent text-muted-foreground hover:border-border hover:text-foreground'
      "
      :style="active === link.key ? { color: `var(${accentVar})`, borderColor: `var(${accentVar})` } : {}"
    >
      {{ link.label }}
    </RouterLink>
  </nav>
</template>
