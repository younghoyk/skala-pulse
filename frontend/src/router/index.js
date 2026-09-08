import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import SurveyRunnerView from '@/views/survey/SurveyRunnerView.vue'
import SurveyCompleteView from '@/views/survey/SurveyCompleteView.vue'
import InstructorCoursesView from '@/views/instructor/InstructorCoursesView.vue'
import InstructorDashboardView from '@/views/instructor/InstructorDashboardView.vue'
import InstructorFeedbacksView from '@/views/instructor/InstructorFeedbacksView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import AdminCourseDetailView from '@/views/admin/AdminCourseDetailView.vue'
import AdminFeedbacksView from '@/views/admin/AdminFeedbacksView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/survey/:id', name: 'survey', component: SurveyRunnerView, props: true },
    { path: '/survey/complete', name: 'survey-complete', component: SurveyCompleteView },
    { path: '/instructor/courses', name: 'instructor-courses', component: InstructorCoursesView },
    { path: '/instructor/dashboard', name: 'instructor-dashboard', component: InstructorDashboardView },
    { path: '/instructor/feedbacks', name: 'instructor-feedbacks', component: InstructorFeedbacksView },
    { path: '/admin/dashboard', name: 'admin-dashboard', component: AdminDashboardView },
    { path: '/admin/dashboard/:courseId', name: 'admin-course-detail', component: AdminCourseDetailView },
    { path: '/admin/feedbacks', name: 'admin-feedbacks', component: AdminFeedbacksView },
  ],
})

export default router
