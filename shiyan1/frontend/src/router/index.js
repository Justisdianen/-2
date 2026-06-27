/**
 * 路由配置文件
 * 文件路径: frontend/src/router/index.js
 */
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'system/user',
        name: 'User',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/menu',
        name: 'Menu',
        component: () => import('@/views/system/Menu.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: 'system/dept',
        name: 'Dept',
        component: () => import('@/views/system/Dept.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  console.log('Route guard - to:', to.path)
  console.log('Route guard - from:', from.path)
  console.log('Route guard - token:', token ? 'exists (' + token.length + ' chars)' : 'null')
  
  if (to.path === '/login') {
    if (token) {
      console.log('Route guard - token exists, redirecting to /home')
      next('/home')
    } else {
      next()
    }
  } else {
    if (!token) {
      console.log('Route guard - no token, redirecting to /login')
      next('/login')
    } else {
      next()
    }
  }
})

export default router