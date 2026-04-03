import { createRouter, createWebHistory } from 'vue-router';
import AdminLayout from '@/layouts/AdminLayout.vue';
import { hasAuthToken } from '@/lib/auth';

const DashboardView = () => import('@/views/DashboardView.vue');
const AppUsersView = () => import('@/views/AppUsersView.vue');
const LoginView = () => import('@/views/LoginView.vue');

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { guestOnly: true }
    },
    {
      path: '/',
      component: AdminLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/dashboard'
        },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: DashboardView
        },
        {
          path: 'app-users',
          name: 'app-users',
          component: AppUsersView
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ],
  scrollBehavior() {
    return { top: 0 };
  }
});

router.beforeEach((to) => {
  const authed = hasAuthToken();

  if (to.matched.some((record) => record.meta.requiresAuth) && !authed) {
    return {
      path: '/login',
      query: to.fullPath === '/' ? undefined : { redirect: to.fullPath }
    };
  }

  if (to.meta.guestOnly && authed) {
    return { path: '/dashboard' };
  }

  return true;
});

export default router;
