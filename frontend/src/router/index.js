import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import SurveyRunnerView from '@/views/survey/SurveyRunnerView.vue'
import SurveyCompleteView from '@/views/survey/SurveyCompleteView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/survey/:id', name: 'survey', component: SurveyRunnerView, props: true },
    { path: '/survey/complete', name: 'survey-complete', component: SurveyCompleteView },
  ],
})

export default router
