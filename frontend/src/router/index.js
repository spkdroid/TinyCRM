import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/HelloWorld.vue';
import FileTicket from '../components/FileTicket.vue';
import TicketComments from '../components/TicketComment.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/file',
    name: 'FileTicket',
    component: FileTicket,
  },
  {
    path: '/ticket',
    name: 'TicketComments',
    component: TicketComments,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
