const Layout = () => import('@/layouts/Layout.vue')

export const userRoutes = [
  {
    path: '/',
    redirect: '/shop-mall',
    hidden: true,
  },
  {
    path: '/shop-mall',
    name: 'shopMall',
    component: Layout,
    meta: {
      title: '商城中心',
      isSingle: true,
      icon: 'icon-storage',
    },
    children: [
      {
        path: '',
        component: () => import('@/views/shop/shop-mall.vue'),
        meta: {
          title: '商城中心',
        },
      },
    ],
  },
  {
    path: '/shop-cart',
    name: 'shopCart',
    component: Layout,
    meta: {
      title: '抢购中心',
      isSingle: true,
      icon: 'icon-fire',
    },
    children: [
      {
        path: '',
        component: () => import('@/views/shop/shop-cart.vue'),
        meta: {
          title: '抢购中心',
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
]