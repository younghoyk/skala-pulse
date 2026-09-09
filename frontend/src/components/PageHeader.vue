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
  <div class="mb-6">
    <div class="flex items-start justify-between gap-3">
      <div>
        <p class="text-[11px] font-semibold uppercase tracking-wider text-muted-foreground">{{ eyebrow }}</p>
        <h1 class="mt-0.5 text-2xl font-bold tracking-tight text-foreground">{{ title }}</h1>
        <RouterLink
          v-if="backTo"
          :to="backTo"
          class="mt-0.5 inline-flex items-center gap-1 text-xs font-medium hover:underline"
          :style="{ color: `var(${accentVar})` }"
        >
          ← {{ backLabel }}
        </RouterLink>
      </div>
      <slot name="actions" />
    </div>
    <p v-if="description" class="mt-1.5 text-sm text-muted-foreground">{{ description }}</p>
  </div>
</template>
