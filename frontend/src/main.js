import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createStore } from 'vuex'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'

// Configure axios
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers.common['Content-Type'] = 'application/json'

// Vuex store
const store = createStore({
  state: {
    user: null,
    tickets: [],
    loading: false,
    theme: 'light'
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TICKETS(state, tickets) {
      state.tickets = tickets
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    SET_THEME(state, theme) {
      state.theme = theme
    }
  },
  actions: {
    async fetchTickets({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get('/api/tickets')
        commit('SET_TICKETS', response.data)
      } catch (error) {
        console.error('Error fetching tickets:', error)
      } finally {
        commit('SET_LOADING', false)
      }
    }
  }
})

const app = createApp(App)

// Register Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(store)
app.use(ElementPlus)
app.config.globalProperties.$http = axios
app.mount('#app')
