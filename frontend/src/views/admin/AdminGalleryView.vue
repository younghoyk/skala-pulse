<script setup>
import { computed, ref } from 'vue'
import { mockAdminFeedbackByCourse } from '@/data/mockAdminFeedback'
import AdminNav from '@/components/admin/AdminNav.vue'
import FeedbackGalleryCard from '@/components/dashboard/FeedbackGalleryCard.vue'

const courseNames = Object.keys(mockAdminFeedbackByCourse)
const selectedCourse = ref('전체')

// "전체"일 때만 과목 라벨을 붙인다 — 과목 하나만 볼 땐 헤더에 이미 나와있어서 중복 표시 안 함.
const activeGallery = computed(() =>
  selectedCourse.value === '전체'
    ? courseNames.flatMap((name) =>
        mockAdminFeedbackByCourse[name].feedbackGallery.map((item) => ({ ...item, courseName: name })),
      )
    : mockAdminFeedbackByCourse[selectedCourse.value].feedbackGallery,
)
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-1">
        <p class="text-xs font-medium text-muted-foreground">시스템 관리자</p>
        <h1 class="text-xl font-bold text-foreground">전체 과정 피드백 갤러리</h1>
      </div>
      <p class="mb-5 text-xs text-muted-foreground">과목별로 필터링해서 볼 수 있습니다.</p>

      <AdminNav active="gallery" />

      <div class="mb-5">
        <select
          v-model="selectedCourse"
          class="rounded-lg border border-border bg-card px-3 py-2 text-sm text-foreground outline-none"
        >
          <option value="전체">전체 과목</option>
          <option v-for="name in courseNames" :key="name" :value="name">{{ name }}</option>
        </select>
      </div>

      <FeedbackGalleryCard :items="activeGallery" />
    </div>
  </div>
</template>
