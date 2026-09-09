<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { apiFetch } from '@/lib/api'
import AdminCourseNav from '@/components/admin/AdminCourseNav.vue'
import PageHeader from '@/components/PageHeader.vue'
import FeedbackGalleryCard from '@/components/dashboard/FeedbackGalleryCard.vue'

const route = useRoute()
const analysis = ref(null)
const loading = ref(false)

async function load() {
  loading.value = true
  analysis.value = null
  try {
    analysis.value = await apiFetch(`/api/courses/${route.params.courseId}/analysis`)
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => route.params.courseId, load)

const hasGallery = computed(() => (analysis.value?.gallery.length ?? 0) > 0)
</script>

<template>
  <div v-if="analysis" class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <PageHeader
        eyebrow="시스템 관리자"
        :title="analysis.courseName"
        :back-to="{ name: 'admin-dashboard' }"
        back-label="전체 강의 목록으로"
      />

      <AdminCourseNav :course-id="route.params.courseId" active="gallery" />

      <FeedbackGalleryCard v-if="hasGallery" :items="analysis.gallery" />
      <div v-else class="rounded-xl border border-border bg-card p-5 text-sm text-muted-foreground shadow-sm">
        아직 쌓인 피드백이 없어요.
      </div>
    </div>
  </div>
  <p v-else-if="loading" class="py-10 text-center text-sm text-muted-foreground">불러오는 중...</p>
</template>
