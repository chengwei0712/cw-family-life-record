import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: '🏠' }
      },
      {
        path: 'media',
        name: 'Media',
        component: () => import('@/views/MediaPage.vue'),
        meta: { title: '照片视频', icon: '📷' }
      },
      {
        path: 'diary',
        name: 'Diary',
        component: () => import('@/views/DiaryPage.vue'),
        meta: { title: '日记本', icon: '📔' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/ProfilePage.vue'),
        meta: { title: '个人中心', icon: '👤' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 家庭记忆` : '家庭记忆'
  
  // 如果去登录页
  if (to.path === '/login') {
    if (isLoggedIn) {
      // 已登录则跳转到首页
      next('/dashboard')
    } else {
      next()
    }
  } else {
    // 需要登录的页面
    if (isLoggedIn) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router