import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/students' },
  { path: '/login', component: () => import('@/pages/Login.vue') },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      // 学生管理
      { path: '/students', component: () => import('@/pages/students/StudentList.vue') },
      { path: '/students/new', component: () => import('@/pages/students/StudentForm.vue') },
      { path: '/students/:id/edit', component: () => import('@/pages/students/StudentForm.vue') },
      { path: '/students/:id', component: () => import('@/pages/students/StudentDetail.vue') },
      // 班级管理
      { path: '/classes', component: () => import('@/pages/classes/ClassList.vue') },
      { path: '/classes/new', component: () => import('@/pages/classes/ClassForm.vue') },
      { path: '/classes/:id/edit', component: () => import('@/pages/classes/ClassForm.vue') },
      { path: '/classes/:id', component: () => import('@/pages/classes/ClassDetail.vue') },
      // 课程管理
      { path: '/courses', component: () => import('@/pages/courses/CourseList.vue') },
      { path: '/courses/new', component: () => import('@/pages/courses/CourseForm.vue') },
      { path: '/courses/:id/edit', component: () => import('@/pages/courses/CourseForm.vue') },
      // AI 智能助手
      { path: '/ai-chat', component: () => import('@/pages/ai/AiChat.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) return next('/login')
  if (to.path === '/login' && token) return next('/students')
  next()
})

export default router
