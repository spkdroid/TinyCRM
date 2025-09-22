import { createRouter, createWebHistory } from 'vue-router';
import DashboardView from '../components/Dashboard.vue';
import FileTicket from '../components/FileTicket.vue';
import TicketsList from '../components/TicketsList.vue';
import TicketComments from '../components/TicketComment.vue';

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: DashboardView,
  },
  {
    path: '/file',
    name: 'FileTicket',
    component: FileTicket,
  },
  {
    path: '/tickets',
    name: 'TicketsList',
    component: TicketsList,
  },
  {
    path: '/ticket/:id?',
    name: 'TicketComments',
    component: TicketComments,
  },
  {
    path: '/reports/overview',
    name: 'ReportsOverview',
    component: DashboardView, // Placeholder - you can create a proper reports component
  },
  {
    path: '/reports/analytics',
    name: 'ReportsAnalytics',
    component: DashboardView, // Placeholder
  },
  {
    path: '/settings',
    name: 'Settings',
    component: DashboardView, // Placeholder
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
