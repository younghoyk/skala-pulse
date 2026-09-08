<script setup>
import { computed } from 'vue'

const props = defineProps({
  courseId: { type: String, required: true },
  active: { type: String, required: true }, // 'dashboard' | 'keywords' | 'gallery'
})

// 강사 쪽 InstructorNav는 스토어로 "지금 강의"를 들고 있지만, 관리자는 :courseId
// 라우트 파라미터로 과목을 표현하므로 링크마다 그 값을 그대로 넣어준다.
const links = computed(() => [
  { key: 'dashboard', label: '대시보드', to: { name: 'admin-course-detail', params: { courseId: props.courseId } } },
  { key: 'keywords', label: '키워드', to: { name: 'admin-course-keywords', params: { courseId: props.courseId } } },
  { key: 'gallery', label: '피드백 갤러리', to: { name: 'admin-course-gallery', params: { courseId: props.courseId } } },
])
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
