import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginView from '@/views/LoginView.vue'
import SignupView from '@/views/SignupView.vue'
import ForbiddenView from '@/views/ForbiddenView.vue'
import SurveyCoursesView from '@/views/survey/SurveyCoursesView.vue'
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
    { path: '/signup', name: 'signup', component: SignupView },
    { path: '/403', name: 'forbidden', component: ForbiddenView },
    { path: '/survey', name: 'survey-courses', component: SurveyCoursesView, meta: { role: 'student' } },
    { path: '/survey/:id', name: 'survey', component: SurveyRunnerView, props: true, meta: { role: 'student' } },
    { path: '/survey/complete', name: 'survey-complete', component: SurveyCompleteView, meta: { role: 'student' } },
    {
      path: '/instructor/courses',
      name: 'instructor-courses',
      component: InstructorCoursesView,
      meta: { role: 'instructor' },
    },
    {
      path: '/instructor/dashboard',
      name: 'instructor-dashboard',
      component: InstructorDashboardView,
      meta: { role: 'instructor' },
    },
    {
      path: '/instructor/keywords',
      name: 'instructor-keywords',
      component: InstructorKeywordsView,
      meta: { role: 'instructor' },
    },
    {
      path: '/instructor/feedback-gallery',
      name: 'instructor-gallery',
      component: InstructorGalleryView,
      meta: { role: 'instructor' },
    },
    {
      path: '/instructor/action-memo',
      name: 'instructor-memo',
      component: InstructorMemoView,
      meta: { role: 'instructor' },
    },
    { path: '/admin/dashboard', name: 'admin-dashboard', component: AdminDashboardView, meta: { role: 'admin' } },
    {
      path: '/admin/dashboard/:courseId',
      name: 'admin-course-detail',
      component: AdminCourseDetailView,
      meta: { role: 'admin' },
    },
    {
      path: '/admin/dashboard/:courseId/keywords',
      name: 'admin-course-keywords',
      component: AdminCourseKeywordsView,
      meta: { role: 'admin' },
    },
    {
      path: '/admin/dashboard/:courseId/gallery',
      name: 'admin-course-gallery',
      component: AdminCourseGalleryView,
      meta: { role: 'admin' },
    },
    {
      path: '/admin/dashboard/:courseId/survey-builder',
      name: 'admin-course-survey-builder',
      component: AdminSurveyBuilderView,
      meta: { role: 'admin' },
    },
    {
      path: '/admin/improvements',
      name: 'admin-improvements',
      component: AdminImprovementsView,
      meta: { role: 'admin' },
    },
    { path: '/admin/members', name: 'admin-members', component: AdminMembersView, meta: { role: 'admin' } },
    { path: '/admin/courses', name: 'admin-courses', component: AdminCoursesView, meta: { role: 'admin' } },
  ],
})

// 로그인 안 했으면 /login, 다른 역할 라우트에 URL로 직접 들어오면 /403으로 보낸다.
// 데모 로그인이라 role은 새로고침하면 초기화되므로, 새로고침 후 보호 라우트 접근도
// 자연스럽게 /login으로 튕긴다 — 실제 인증 흐름과 같은 모양이다.
router.beforeEach((to) => {
  if (!to.meta.role) return true
  const auth = useAuthStore()
  if (!auth.role) return { name: 'login' }
  if (auth.role !== to.meta.role) return { name: 'forbidden' }
  return true
})

export default router
