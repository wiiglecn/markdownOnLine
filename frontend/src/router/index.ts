import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('../pages/LoginPage.vue'),
    },
    {
      path: '/register',
      component: () => import('../pages/RegisterPage.vue'),
    },
    {
      path: '/files',
      component: () => import('../pages/FileListPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/editor/:fileId',
      component: () => import('../pages/EditorPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/',
      redirect: '/files',
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  auth.loadFromStorage()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/login'
  }
})

export default router
