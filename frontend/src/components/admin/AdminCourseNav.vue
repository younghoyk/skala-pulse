<script setup>
import { computed } from 'vue'
import TabNav from '@/components/TabNav.vue'

const props = defineProps({
  courseId: { type: String, required: true },
  active: { type: String, required: true }, // 'dashboard' | 'keywords' | 'gallery' | 'builder'
})

// 강사 쪽 InstructorNav는 스토어로 "지금 강의"를 들고 있지만, 관리자는 :courseId
// 라우트 파라미터로 과목을 표현하므로 링크마다 그 값을 그대로 넣어준다.
const links = computed(() => [
  { key: 'dashboard', label: '대시보드', to: { name: 'admin-course-detail', params: { courseId: props.courseId } } },
  { key: 'keywords', label: '키워드', to: { name: 'admin-course-keywords', params: { courseId: props.courseId } } },
  { key: 'gallery', label: '피드백 갤러리', to: { name: 'admin-course-gallery', params: { courseId: props.courseId } } },
  { key: 'builder', label: '설문 빌더', to: { name: 'admin-course-survey-builder', params: { courseId: props.courseId } } },
])
</script>

<template>
  <TabNav :links="links" :active="active" accent-var="--role-admin" />
</template>
