<script setup>
import { mockInstructorClass } from '@/data/mockInstructorData'
import { mockFeedbackGallery } from '@/data/mockInstructorFeedback'
import InstructorNav from '@/components/instructor/InstructorNav.vue'
import KeywordGraphCard from '@/components/dashboard/KeywordGraphCard.vue'
import FeedbackGalleryCard from '@/components/dashboard/FeedbackGalleryCard.vue'
import ActionMemoCard from '@/components/dashboard/ActionMemoCard.vue'

const data = mockInstructorClass
</script>

<template>
  <div class="min-h-svh bg-muted/40 px-4 py-10">
    <div class="mx-auto max-w-3xl">
      <div class="mb-4">
        <p class="text-xs font-medium text-muted-foreground">{{ data.className }}</p>
        <h1 class="text-xl font-bold text-foreground">{{ data.courseName }} · 피드백</h1>
      </div>

      <InstructorNav active="feedbacks" />

      <div class="flex flex-col gap-5">
        <KeywordGraphCard />
        <div class="grid grid-cols-1 gap-5 md:grid-cols-2">
          <FeedbackGalleryCard :items="mockFeedbackGallery" />
          <ActionMemoCard />
        </div>
      </div>
    </div>
  </div>
</template>
