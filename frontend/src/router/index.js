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
import AdminCourseKeywordsView from '@/views/admin/AdminCourseKeywordsView.vue'
import AdminCourseGalleryView from '@/views/admin/AdminCourseGalleryView.vue'
import AdminImprovementsView from '@/views/admin/AdminImprovementsView.vue'
import AdminMembersView from '@/views/admin/AdminMembersView.vue'
import AdminCoursesView from '@/views/admin/AdminCoursesView.vue'
import AdminSurveyBuilderView from '@/views/admin/AdminSurveyBuilderView.vue'

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
    { path: '/admin/dashboard/:courseId/keywords', name: 'admin-course-keywords', component: AdminCourseKeywordsView },
    { path: '/admin/dashboard/:courseId/gallery', name: 'admin-course-gallery', component: AdminCourseGalleryView },
    {
      path: '/admin/dashboard/:courseId/survey-builder',
      name: 'admin-course-survey-builder',
      component: AdminSurveyBuilderView,
    },
    { path: '/admin/improvements', name: 'admin-improvements', component: AdminImprovementsView },
    { path: '/admin/members', name: 'admin-members', component: AdminMembersView },
    { path: '/admin/courses', name: 'admin-courses', component: AdminCoursesView },
  ],
})

export default router
