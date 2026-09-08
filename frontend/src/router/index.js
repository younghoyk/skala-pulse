import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import SurveyRunnerView from '@/views/survey/SurveyRunnerView.vue'
import SurveyCompleteView from '@/views/survey/SurveyCompleteView.vue'
import InstructorCoursesView from '@/views/instructor/InstructorCoursesView.vue'
import InstructorDashboardView from '@/views/instructor/InstructorDashboardView.vue'
import InstructorKeywordsView from '@/views/instructor/InstructorKeywordsView.vue'
import InstructorGalleryView from '@/views/instructor/InstructorGalleryView.vue'
import InstructorMemoView from '@/views/instructor/InstructorMemoView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import AdminCourseDetailView from '@/views/admin/AdminCourseDetailView.vue'
import AdminKeywordsView from '@/views/admin/AdminKeywordsView.vue'
import AdminGalleryView from '@/views/admin/AdminGalleryView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/survey/:id', name: 'survey', component: SurveyRunnerView, props: true },
    { path: '/survey/complete', name: 'survey-complete', component: SurveyCompleteView },
    { path: '/instructor/courses', name: 'instructor-courses', component: InstructorCoursesView },
    { path: '/instructor/dashboard', name: 'instructor-dashboard', component: InstructorDashboardView },
    { path: '/instructor/keywords', name: 'instructor-keywords', component: InstructorKeywordsView },
    { path: '/instructor/feedback-gallery', name: 'instructor-gallery', component: InstructorGalleryView },
    { path: '/instructor/action-memo', name: 'instructor-memo', component: InstructorMemoView },
    { path: '/admin/dashboard', name: 'admin-dashboard', component: AdminDashboardView },
    { path: '/admin/dashboard/:courseId', name: 'admin-course-detail', component: AdminCourseDetailView },
    { path: '/admin/keywords', name: 'admin-keywords', component: AdminKeywordsView },
    { path: '/admin/feedback-gallery', name: 'admin-gallery', component: AdminGalleryView },
  ],
})

export default router
