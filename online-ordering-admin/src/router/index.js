import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('@/views/Dish/Index.vue'),
        meta: { title: '菜品管理' }
      },
      {
        path: 'dish/edit',
        name: 'DishEdit',
        component: () => import('@/views/Dish/Edit.vue'),
        meta: { title: '菜品编辑' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/Category/Index.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'setmeal',
        name: 'Setmeal',
        component: () => import('@/views/Setmeal/Index.vue'),
        meta: { title: '套餐管理' }
      },
      {
        path: 'setmeal/edit',
        name: 'SetmealEdit',
        component: () => import('@/views/Setmeal/Edit.vue'),
        meta: { title: '套餐编辑' }
      },
      {
        path: 'table',
        name: 'Table',
        component: () => import('@/views/Table/Index.vue'),
        meta: { title: '餐台管理' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/Order/Index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: () => import('@/views/Customer/Index.vue'),
        meta: { title: '顾客管理' }
      },
      {
        path: 'customer/detail/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/Customer/Detail.vue'),
        meta: { title: '顾客详情' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title || '在线点餐后台管理系统'
  
  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 如果没有 token，跳转到登录页
  if (!userStore.token) {
    next('/login')
    return
  }
  
  next()
})

export default router
