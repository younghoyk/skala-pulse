<script setup>
defineProps({
  eyebrow: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  backTo: { type: [String, Object], default: null },
  backLabel: { type: String, default: '' },
  accentVar: { type: String, default: '--role-admin' },
})
</script>

<template>
  <div class="mb-5">
    <div class="flex items-start justify-between gap-3">
      <div>
        <p class="text-xs font-medium text-muted-foreground">{{ eyebrow }}</p>
        <h1 class="text-xl font-bold text-foreground">{{ title }}</h1>
        <RouterLink v-if="backTo" :to="backTo" class="text-xs underline" :style="{ color: `var(${accentVar})` }">
          {{ backLabel }}
        </RouterLink>
      </div>
      <slot name="actions" />
    </div>
    <p v-if="description" class="mt-1 text-xs text-muted-foreground">{{ description }}</p>
  </div>
</template>
