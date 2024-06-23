import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/Home.vue';
import FileTicket from '../components/FileTicket.vue';
import TicketComments from '../components/TicketComments.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/file-ticket',
    name: 'FileTicket',
    component: FileTicket,
  },
  {
    path: '/ticket-comments',
    name: 'TicketComments',
    component: TicketComments,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
