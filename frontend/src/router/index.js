import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../components/LoginPage.vue';
import DashboardView from '../components/Dashboard.vue';
import FileTicket from '../components/FileTicket.vue';
import TicketsList from '../components/TicketsList.vue';
import TicketComments from '../components/TicketComment.vue';
import About from '../components/About.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginPage,
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { requiresAuth: true },
  },
  {
    path: '/file',
    name: 'FileTicket',
    component: FileTicket,
    meta: { requiresAuth: true },
  },
  {
    path: '/tickets',
    name: 'TicketsList',
    component: TicketsList,
    meta: { requiresAuth: true },
  },
  {
    path: '/ticket/:id?',
    name: 'TicketComments',
    component: TicketComments,
    meta: { requiresAuth: true },
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { requiresAuth: true },
  },
  {
    path: '/reports/overview',
    name: 'ReportsOverview',
    component: DashboardView, // Placeholder - you can create a proper reports component
    meta: { requiresAuth: true },
  },
  {
    path: '/reports/analytics',
    name: 'ReportsAnalytics',
    component: DashboardView, // Placeholder
    meta: { requiresAuth: true },
  },
  {
    path: '/settings',
    name: 'Settings',
    component: DashboardView, // Placeholder
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Authentication guard
router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const sessionToken = localStorage.getItem('sessionToken');
  
  if (requiresAuth && !sessionToken) {
    // Redirect to login if authentication is required but user is not logged in
    next('/');
  } else if (to.path === '/' && sessionToken) {
    // If user is logged in and tries to access login page, redirect to dashboard
    try {
      const response = await fetch('/api/auth/me', {
        method: 'GET',
        credentials: 'include'
      });
      const data = await response.json();
      
      if (data.success) {
        next('/dashboard');
      } else {
        localStorage.removeItem('sessionToken');
        localStorage.removeItem('user');
        next();
      }
    } catch (error) {
      localStorage.removeItem('sessionToken');
      localStorage.removeItem('user');
      next();
    }
  } else {
    next();
  }
});

export default router;
