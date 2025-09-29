<template>
  <div id="app">
    <!-- Show login page or main app based on authentication -->
    <router-view v-if="$route.name === 'Login'" />
    
    <el-container v-else class="app-container">
      <!-- Header -->
      <el-header class="app-header">
        <div class="header-left">
          <div class="app-icon-main">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="28" height="28" rx="2" fill="#0078d4"/>
              <path d="M7 9h14v2H7V9zm0 3h12v2H7v-2zm0 3h10v2H7v-2z" fill="white"/>
              <circle cx="21" cy="19" r="2.5" fill="white"/>
            </svg>
          </div>
          <h1 class="app-title">TinyCRM</h1>
        </div>
        <div class="header-right">
          <span class="user-name">{{ currentUser?.firstName || currentUser?.username }}</span>
          <el-dropdown @command="handleUserAction">
            <el-avatar :size="40" :src="userAvatar" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  Profile
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  Settings
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  Logout
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- Sidebar -->
        <el-aside width="240px" class="app-sidebar">
          <el-menu
            :default-active="$route.path"
            class="sidebar-menu"
            router
            unique-opened
          >
            <el-menu-item index="/dashboard">
              <el-icon><HomeFilled /></el-icon>
              <span>Dashboard</span>
            </el-menu-item>
            <el-menu-item index="/tickets">
              <el-icon><Tickets /></el-icon>
              <span>All Tickets</span>
            </el-menu-item>
            <el-menu-item index="/file">
              <el-icon><DocumentAdd /></el-icon>
              <span>Create Ticket</span>
            </el-menu-item>
            <el-menu-item index="/messages">
              <el-icon><ChatDotRound /></el-icon>
              <span>Messages</span>
            </el-menu-item>
            <el-sub-menu index="reports">
              <template #title>
                <el-icon><DataAnalysis /></el-icon>
                <span>Reports</span>
              </template>
              <el-menu-item index="/reports/overview">Overview</el-menu-item>
              <el-menu-item index="/reports/analytics">Analytics</el-menu-item>
            </el-sub-menu>
            <el-menu-item index="/settings">
              <el-icon><Setting /></el-icon>
              <span>Settings</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- Main Content -->
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const currentUser = ref(null)
    const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

    const loadCurrentUser = () => {
      const userData = localStorage.getItem('user')
      if (userData) {
        currentUser.value = JSON.parse(userData)
      }
    }

    const handleUserAction = async (command) => {
      switch (command) {
        case 'profile':
          ElMessage.info('Profile feature coming soon!')
          break
        case 'settings':
          router.push('/settings')
          break
        case 'logout':
          try {
            const result = await ElMessageBox.confirm(
              'Are you sure you want to logout?',
              'Confirm Logout',
              {
                confirmButtonText: 'Yes, Logout',
                cancelButtonText: 'Cancel',
                type: 'warning'
              }
            )
            
            if (result === 'confirm') {
              await logout()
            }
          } catch (error) {
            // User cancelled
          }
          break
      }
    }

    const logout = async () => {
      try {
        await axios.post('/api/auth/logout')
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        // Clear local storage
        localStorage.removeItem('user')
        localStorage.removeItem('sessionToken')
        currentUser.value = null
        
        // Redirect to login
        router.push('/')
        
        ElMessage.success('Logged out successfully')
      }
    }

    onMounted(() => {
      loadCurrentUser()
    })

    return {
      currentUser,
      userAvatar,
      handleUserAction
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  height: 100vh;
}

.app-container {
  height: 100vh;
}

.app-header {
  background: #f3f2f1;
  border-bottom: 1px solid #d1d1d1;
  color: #323130;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  height: 48px;
  margin-right: 20px;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0;
  color: #323130;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  font-weight: 400;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #323130;
  margin-right: 8px;
}

.header-right :deep(.el-avatar) {
  border: 3px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
}

.header-right :deep(.el-avatar:hover) {
  border-color: rgba(255, 255, 255, 0.4);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.app-sidebar {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.08);
  border-right: 1px solid rgba(102, 126, 234, 0.1);
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
  background: transparent;
  padding: 16px 0;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 4px 16px;
  border-radius: 16px;
  height: 48px;
  line-height: 48px;
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(4px);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.sidebar-menu :deep(.el-sub-menu) {
  margin: 4px 16px;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  border-radius: 16px;
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-color: rgba(102, 126, 234, 0.2);
}

.app-main {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 32px;
  overflow-y: auto;
  min-height: calc(100vh - 60px);
  position: relative;
}

.app-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  pointer-events: none;
  z-index: 0;
}

.app-main > * {
  position: relative;
  z-index: 1;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>
