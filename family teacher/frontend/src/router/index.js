import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/student/home',
    name: 'StudentHome',
    component: () => import('../views/student/HomeView.vue')
  },
  {
    path: '/student/profile',
    name: 'StudentProfile',
    component: () => import('../views/student/ProfileView.vue')
  },
  {
    path: '/student/tutoring-request',
    name: 'StudentTutoringRequest',
    component: () => import('../views/student/TutoringRequestView.vue')
  },
  {
    path: '/student/appointments',
    name: 'StudentAppointments',
    component: () => import('../views/student/AppointmentsView.vue')
  },
  {
    path: '/student/favorites',
    name: 'StudentFavorites',
    component: () => import('../views/student/FavoritesView.vue')
  },
  {
    path: '/student/evaluations',
    name: 'StudentEvaluations',
    component: () => import('../views/student/EvaluationsView.vue')
  },
  {
    path: '/teacher/home',
    name: 'TeacherHome',
    component: () => import('../views/teacher/HomeView.vue')
  },
  {
    path: '/teacher/profile',
    name: 'TeacherProfile',
    component: () => import('../views/teacher/ProfileView.vue')
  },
  {
    path: '/teacher/job-post',
    name: 'TeacherJobPost',
    component: () => import('../views/teacher/JobPostView.vue')
  },
  {
    path: '/teacher/appointments',
    name: 'TeacherAppointments',
    component: () => import('../views/teacher/AppointmentsView.vue')
  },
  {
    path: '/teacher/favorites',
    name: 'TeacherFavorites',
    component: () => import('../views/teacher/FavoritesView.vue')
  },
  {
    path: '/teacher/evaluations',
    name: 'TeacherEvaluations',
    component: () => import('../views/teacher/EvaluationsView.vue')
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/DashboardView.vue')
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/admin/UsersView.vue')
  },
  {
    path: '/admin/appointments',
    name: 'AdminAppointments',
    component: () => import('../views/admin/AppointmentsView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
