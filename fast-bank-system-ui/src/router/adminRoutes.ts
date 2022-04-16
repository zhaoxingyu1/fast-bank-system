const Layout = () => import('@/layouts/Layout.vue')

export const adminRoutes = [
  {
    path: '/',
    redirect: '/admin/home',
    hidden: true,
  },
  {
    path: '/admin',
    component: Layout,
    name: 'Index',
    meta: {
      title: 'Dashboard',
      // iconPrefix: 'iconfont',
      icon: 'icon-dashboard',
    },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: (): any => import('@/views/index/main.vue'),
        meta: {
          title: '主控台',
          affix: true,
          cacheable: true,
          icon: 'icon-computer',
        },
      },
      {
        path: 'work-place',
        name: 'WorkPlace',
        component: (): any => import('@/views/index/work-place.vue'),
        meta: {
          title: '工作台',
          icon: 'icon-command',
        },
      },
    ],
  },
  {
    path: '/order',
    name: 'order',
    component: Layout,
    meta: {
      title: '订单中心',
      isSingle: true,
      icon: 'icon-stamp',
    },
    children: [
      {
        path: '/all-order',
        component: () => import('@/views/products/all-order.vue'),
        meta: {
          title: '订单中心',
        },
      }
    ],
  },
  {
    path: '/products',
    name: 'products',
    component: Layout,
    meta: {
      title: '产品中心',
      isSingle: true,
      icon: 'icon-apps',
    },
    children: [
      {
        path: '/all-products',
        component: () => import('@/views/products/all-products.vue'),
        meta: {
          title: '产品中心',
        },
      },
    ],
  },
  {
    path: '/user-management',
    name: 'userManagement',
    component: Layout,
    meta: {
      title: '用户管理',
      isSingle: true,
      icon: 'icon-user-group',
    },
    children: [
      {
        path: '',
        component: () => import('@/views/system/user.vue'),
        meta: {
          title: '用户管理',
        },
      },
    ]
  },
]
