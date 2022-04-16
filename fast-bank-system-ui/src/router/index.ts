import { mapTwoLevelRouter } from '@/utils'
import { createRouter, createWebHistory } from 'vue-router'

const Layout = () => import('@/layouts/Layout.vue')

export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    hidden: true,
    component: () => import('@/views/login/index.vue'),
  },
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    meta: {
      noShowTabbar: true,
    },
    children: [
      {
        path: '/redirect/:path(.*)*',
        component: (): any => import('@/views/redirect/index.vue'),
      },
    ],
  },
  {
    path: '/personal',
    name: 'personal',
    component: Layout,
    hidden: true,
    meta: {
      title: '个人中心',
      isSingle: true,
    },
    children: [
      {
        path: '',
        component: () => import('@/views/personal/index.vue'),
        meta: {
          title: '个人中心',
        },
      },
    ],
  },
  {
    path: '/params-info',
    name: 'paramsInfo',
    component: Layout,
    hidden: true,
    meta: {
      title: '路由参数',
    },
    children: [
      {
        path: 'query',
        component: () => import('@/views/route-params/query-details.vue'),
        meta: {
          title: 'query参数详情',
        },
      },
      {
        path: 'params/:id',
        name: 'paramsDetails',
        component: () => import('@/views/route-params/params-details.vue'),
        meta: {
          title: 'params参数详情',
        },
      },
    ],
  },
  {
    path: '/404',
    name: '404',
    hidden: true,
    component: () => import('@/views/exception/404.vue'),
  },
  {
    path: '/500',
    name: '500',
    hidden: true,
    component: () => import('@/views/exception/500.vue'),
  },
  {
    path: '/403',
    name: '403',
    hidden: true,
    component: () => import('@/views/exception/403.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: mapTwoLevelRouter(constantRoutes),
})

export default router
