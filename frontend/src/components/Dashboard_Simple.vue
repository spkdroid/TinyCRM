<template>
  <div class="dashboard">
    <!-- Enhanced Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h2>Dashboard Overview</h2>
          <p>Welcome back, {{ currentUser?.firstName || 'User' }}! Here's what's happening in your CRM.</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="navigateToCreateTicket" class="header-btn">
            <el-icon><Plus /></el-icon>
            New Ticket
          </el-button>
          <el-dropdown @command="handleQuickAction" class="quick-menu">
            <el-button type="info" plain>
              <el-icon><MoreFilled /></el-icon>
              Quick Actions
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="export">
                  <el-icon><Download /></el-icon>
                  Export Data
                </el-dropdown-item>
                <el-dropdown-item command="import">
                  <el-icon><Upload /></el-icon>
                  Import Data
                </el-dropdown-item>
                <el-dropdown-item command="settings" divided>
                  <el-icon><Setting /></el-icon>
                  Settings
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div class="time-filters">
        <el-radio-group v-model="timeFilter" @change="onTimeFilterChange" size="small">
          <el-radio-button label="today">Today</el-radio-button>
          <el-radio-button label="week">This Week</el-radio-button>
          <el-radio-button label="month">This Month</el-radio-button>
          <el-radio-button label="quarter">Quarter</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- Enhanced Statistics Cards -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card total-card" @click="navigateToTickets">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon size="28"><Tickets /></el-icon>
            </div>
            <div class="stat-details">
              <div class="stat-number">{{ totalTickets }}</div>
              <div class="stat-label">Total Tickets</div>
              <div class="stat-trend positive">
                <el-icon size="12"><CaretTop /></el-icon>
                +{{ ticketTrend }}% vs last {{ timeFilter }}
              </div>
            </div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="100" :show-text="false" :stroke-width="3" color="#667eea" />
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card open-card" @click="navigateToOpenTickets">
          <div class="stat-content">
            <div class="stat-icon open">
              <el-icon size="28"><Warning /></el-icon>
            </div>
            <div class="stat-details">
              <div class="stat-number">{{ openTickets }}</div>
              <div class="stat-label">Open Tickets</div>
              <div class="stat-trend negative">
                <el-icon size="12"><CaretBottom /></el-icon>
                -{{ Math.abs(openTrend) }}% vs last {{ timeFilter }}
              </div>
            </div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="openPercentage" :show-text="false" :stroke-width="3" color="#f56565" />
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card progress-card" @click="navigateToInProgressTickets">
          <div class="stat-content">
            <div class="stat-icon progress">
              <el-icon size="28"><Loading /></el-icon>
            </div>
            <div class="stat-details">
              <div class="stat-number">{{ inProgressTickets }}</div>
              <div class="stat-label">In Progress</div>
              <div class="stat-trend positive">
                <el-icon size="12"><CaretTop /></el-icon>
                +{{ progressTrend }}% vs last {{ timeFilter }}
              </div>
            </div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="progressPercentage" :show-text="false" :stroke-width="3" color="#4facfe" />
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card resolved-card" @click="navigateToResolvedTickets">
          <div class="stat-content">
            <div class="stat-icon resolved">
              <el-icon size="28"><CircleCheck /></el-icon>
            </div>
            <div class="stat-details">
              <div class="stat-number">{{ resolvedTickets }}</div>
              <div class="stat-label">Resolved</div>
              <div class="stat-trend positive">
                <el-icon size="12"><CaretTop /></el-icon>
                +{{ resolvedTrend }}% vs last {{ timeFilter }}
              </div>
            </div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="resolvedPercentage" :show-text="false" :stroke-width="3" color="#48bb78" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Performance Metrics -->
    <el-row :gutter="24" class="metrics-row">
      <el-col :xs="24" :sm="8">
        <div class="metric-card">
          <div class="metric-header">
            <el-icon class="metric-icon response"><Timer /></el-icon>
            <span class="metric-title">Avg Response Time</span>
          </div>
          <div class="metric-value">{{ avgResponseTime }}h</div>
          <div class="metric-subtitle">{{ responseTimeChange }}% from last {{ timeFilter }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="metric-card">
          <div class="metric-header">
            <el-icon class="metric-icon satisfaction"><Star /></el-icon>
            <span class="metric-title">Customer Satisfaction</span>
          </div>
          <div class="metric-value">{{ customerSatisfaction }}%</div>
          <div class="metric-subtitle">{{ satisfactionChange }}% from last {{ timeFilter }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="metric-card">
          <div class="metric-header">
            <el-icon class="metric-icon resolution"><Trophy /></el-icon>
            <span class="metric-title">Resolution Rate</span>
          </div>
          <div class="metric-value">{{ resolutionRate }}%</div>
          <div class="metric-subtitle">{{ resolutionChange }}% from last {{ timeFilter }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts and Recent Activity -->
    <el-row :gutter="20" class="content-row">
      <!-- Recent Tickets -->
      <el-col :xs="24" :lg="12">
        <el-card class="content-card recent-tickets-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="card-icon"><Tickets /></el-icon>
                <span class="card-title">Recent Tickets</span>
              </div>
              <el-button type="primary" size="small" @click="navigateToTickets" class="view-all-btn">
                <el-icon><ArrowRight /></el-icon>
                View All
              </el-button>
            </div>
          </template>
          <div class="tickets-list" v-loading="loading">
            <div v-for="ticket in recentTickets" :key="ticket.id" class="ticket-item" @click="viewTicket(ticket.id)">
              <div class="ticket-info">
                <div class="ticket-header">
                  <span class="ticket-id">#{{ ticket.id }}</span>
                  <el-tag :type="getPriorityType(ticket.priority)" size="small" class="priority-tag">
                    {{ ticket.priority }}
                  </el-tag>
                </div>
                <div class="ticket-subject">{{ ticket.subject }}</div>
                <div class="ticket-meta">
                  <span class="ticket-status">
                    <el-tag :type="getStatusType(ticket.status)" size="small" effect="plain">
                      {{ ticket.status }}
                    </el-tag>
                  </span>
                  <span class="ticket-date">{{ formatDate(ticket.createdDate) }}</span>
                </div>
              </div>
              <div class="ticket-actions">
                <el-icon class="action-icon"><ArrowRight /></el-icon>
              </div>
            </div>
            <div v-if="recentTickets.length === 0" class="empty-state">
              <el-icon size="48" class="empty-icon"><Document /></el-icon>
              <p>No recent tickets found</p>
              <el-button type="primary" @click="navigateToCreateTicket">Create Your First Ticket</el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- User Management & Quick Actions -->
      <el-col :xs="24" :lg="12">
        <el-card class="content-card user-management-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="card-icon"><User /></el-icon>
                <span class="card-title">User Management</span>
              </div>
              <el-button type="primary" size="small" @click="showCreateUserDialog" class="create-user-btn">
                <el-icon><Plus /></el-icon>
                Add User
              </el-button>
            </div>
          </template>
          <div class="user-management-content">
            <div class="user-stats">
              <div class="user-stat-item">
                <div class="user-stat-number">{{ totalUsers }}</div>
                <div class="user-stat-label">Total Users</div>
              </div>
              <div class="user-stat-item">
                <div class="user-stat-number">{{ activeUsers }}</div>
                <div class="user-stat-label">Active Today</div>
              </div>
              <div class="user-stat-item">
                <div class="user-stat-number">{{ newUsersThisWeek }}</div>
                <div class="user-stat-label">New This Week</div>
              </div>
            </div>
            
            <div class="recent-users">
              <h4>Recent Users</h4>
              <div class="users-list">
                <div v-for="user in recentUsers" :key="user.id" class="user-item">
                  <el-avatar :size="32" :src="user.avatar" class="user-avatar">
                    {{ user.firstName?.[0] }}{{ user.lastName?.[0] }}
                  </el-avatar>
                  <div class="user-info">
                    <div class="user-name">{{ user.firstName }} {{ user.lastName }}</div>
                    <div class="user-email">{{ user.email }}</div>
                  </div>
                  <div class="user-status">
                    <el-tag :type="user.active ? 'success' : 'info'" size="small" effect="plain">
                      {{ user.active ? 'Active' : 'Inactive' }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>

            <!-- Quick Actions -->
            <div class="quick-actions">
              <h4>Quick Actions</h4>
              <div class="actions-grid">
                <div class="action-item" @click="navigateToCreateTicket">
                  <el-icon class="action-icon create"><DocumentAdd /></el-icon>
                  <span>Create Ticket</span>
                </div>
                <div class="action-item" @click="navigateToTickets">
                  <el-icon class="action-icon view"><View /></el-icon>
                  <span>View Tickets</span>
                </div>
                <div class="action-item" @click="navigateToReports">
                  <el-icon class="action-icon report"><DataAnalysis /></el-icon>
                  <span>Reports</span>
                </div>
                <div class="action-item" @click="showSystemStatus">
                  <el-icon class="action-icon status"><Monitor /></el-icon>
                  <span>System Status</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Create User Dialog -->
    <el-dialog v-model="createUserVisible" title="Create New User" width="600px" :close-on-click-modal="false">
      <el-form ref="createUserForm" :model="createUserForm" :rules="createUserRules" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="First Name" prop="firstName">
              <el-input v-model="createUserForm.firstName" placeholder="Enter first name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Last Name" prop="lastName">
              <el-input v-model="createUserForm.lastName" placeholder="Enter last name" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Username" prop="username">
          <el-input v-model="createUserForm.username" placeholder="Enter username" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="createUserForm.email" type="email" placeholder="Enter email address" />
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-select v-model="createUserForm.role" placeholder="Select role" style="width: 100%">
            <el-option label="User" value="USER" />
            <el-option label="Admin" value="ADMIN" />
            <el-option label="Support" value="SUPPORT" />
          </el-select>
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="createUserForm.password" type="password" placeholder="Enter password" show-password />
        </el-form-item>
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input v-model="createUserForm.confirmPassword" type="password" placeholder="Confirm password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createUserVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleCreateUser" :loading="createUserLoading">
            Create User
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- System Status Dialog -->
    <el-dialog v-model="systemStatusVisible" title="System Status" width="500px">
      <div class="system-status-detail">
        <div class="status-section">
          <h4>Core Services</h4>
          <div class="status-item">
            <div class="status-indicator online"></div>
            <span class="status-name">Database Server</span>
            <span class="status-value">Online</span>
          </div>
          <div class="status-item">
            <div class="status-indicator online"></div>
            <span class="status-name">API Gateway</span>
            <span class="status-value">Online</span>
          </div>
          <div class="status-item">
            <div class="status-indicator online"></div>
            <span class="status-name">Email Service</span>
            <span class="status-value">Online</span>
          </div>
        </div>
        <div class="status-section">
          <h4>Performance Metrics</h4>
          <div class="metric-item">
            <span class="metric-name">Server Response Time</span>
            <span class="metric-value">{{ serverResponseTime }}ms</span>
          </div>
          <div class="metric-item">
            <span class="metric-name">Memory Usage</span>
            <span class="metric-value">{{ memoryUsage }}%</span>
          </div>
          <div class="metric-item">
            <span class="metric-name">CPU Usage</span>
            <span class="metric-value">{{ cpuUsage }}%</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { ElMessage, ElNotification } from 'element-plus'
import {
  Plus,
  MoreFilled,
  Download,
  Upload,
  Setting,
  Tickets,
  CaretTop,
  CaretBottom,
  Warning,
  Loading,
  CircleCheck,
  Timer,
  Star,
  Trophy,
  ArrowRight,
  Document,
  User,
  DocumentAdd,
  View,
  DataAnalysis,
  Monitor
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import axios from 'axios'

export default {
  name: 'DashboardView',
  components: {
    Plus,
    MoreFilled,
    Download,
    Upload,
    Setting,
    Tickets,
    CaretTop,
    CaretBottom,
    Warning,
    Loading,
    CircleCheck,
    Timer,
    Star,
    Trophy,
    ArrowRight,
    Document,
    User,
    DocumentAdd,
    View,
    DataAnalysis,
    Monitor
  },
  data() {
    return {
      // Ticket statistics
      totalTickets: 0,
      openTickets: 0,
      inProgressTickets: 0,
      resolvedTickets: 0,
      recentTickets: [],
      
      // Trends and percentages
      ticketTrend: 12,
      openTrend: -8,
      progressTrend: 15,
      resolvedTrend: 22,
      openPercentage: 25,
      progressPercentage: 35,
      resolvedPercentage: 85,
      
      // Performance metrics
      avgResponseTime: 2.4,
      responseTimeChange: -15,
      customerSatisfaction: 94,
      satisfactionChange: 3,
      resolutionRate: 87,
      resolutionChange: 8,
      
      // User management
      totalUsers: 0,
      activeUsers: 0,
      newUsersThisWeek: 0,
      recentUsers: [],
      createUserVisible: false,
      createUserLoading: false,
      
      // System status
      systemStatusVisible: false,
      serverResponseTime: 120,
      memoryUsage: 68,
      cpuUsage: 45,
      
      // Time filter
      timeFilter: 'week',
      
      // Current user
      currentUser: null,
      
      // Create user form
      createUserForm: {
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        role: 'USER',
        password: '',
        confirmPassword: ''
      },
      
      createUserRules: {
        firstName: [{ required: true, message: 'Please enter first name', trigger: 'blur' }],
        lastName: [{ required: true, message: 'Please enter last name', trigger: 'blur' }],
        username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
        email: [
          { required: true, message: 'Please enter email', trigger: 'blur' },
          { type: 'email', message: 'Please enter valid email', trigger: 'blur' }
        ],
        role: [{ required: true, message: 'Please select role', trigger: 'change' }],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' },
          { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: 'Please confirm password', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.createUserForm.password) {
                callback(new Error('Passwords do not match'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    ...mapState(['tickets', 'loading'])
  },
  async mounted() {
    // Load current user
    const userData = localStorage.getItem('user')
    if (userData) {
      this.currentUser = JSON.parse(userData)
    }
    
    await this.loadDashboardData()
  },
  methods: {
    ...mapActions(['fetchTickets']),
    async loadDashboardData() {
      try {
        await this.fetchTickets()
        await this.loadUsers()
        this.calculateStats()
        this.recentTickets = this.tickets.slice(0, 5)
      } catch (error) {
        ElMessage.error('Failed to load dashboard data')
      }
    },
    
    async loadUsers() {
      try {
        const response = await axios.get('/api/users')
        const users = response.data
        this.totalUsers = users.length
        this.activeUsers = users.filter(u => u.active).length
        this.newUsersThisWeek = users.filter(u => {
          const createdDate = dayjs(u.createdAt)
          return createdDate.isAfter(dayjs().subtract(7, 'day'))
        }).length
        this.recentUsers = users.slice(0, 4)
      } catch (error) {
        console.error('Failed to load users:', error)
      }
    },
    
    calculateStats() {
      this.totalTickets = this.tickets.length
      this.openTickets = this.tickets.filter(t => t.status === 'Open').length
      this.inProgressTickets = this.tickets.filter(t => t.status === 'In Progress').length
      this.resolvedTickets = this.tickets.filter(t => t.status === 'Resolved').length
      
      // Calculate percentages
      if (this.totalTickets > 0) {
        this.openPercentage = Math.round((this.openTickets / this.totalTickets) * 100)
        this.progressPercentage = Math.round((this.inProgressTickets / this.totalTickets) * 100)
        this.resolvedPercentage = Math.round((this.resolvedTickets / this.totalTickets) * 100)
      }
    },
    getPriorityType(priority) {
      const types = {
        'High': 'danger',
        'Medium': 'warning',
        'Low': 'success'
      }
      return types[priority] || 'info'
    },
    getStatusType(status) {
      const types = {
        'Open': 'danger',
        'In Progress': 'warning',
        'Resolved': 'success',
        'Closed': 'info'
      }
      return types[status] || 'info'
    },
    formatDate(date) {
      return dayjs(date).format('MMM DD')
    },
    navigateToTickets() {
      this.$router.push('/tickets')
    },
    navigateToOpenTickets() {
      this.$router.push('/tickets?status=open')
    },
    navigateToInProgressTickets() {
      this.$router.push('/tickets?status=progress')
    },
    navigateToResolvedTickets() {
      this.$router.push('/tickets?status=resolved')
    },
    navigateToCreateTicket() {
      this.$router.push('/file')
    },
    navigateToReports() {
      this.$router.push('/reports/overview')
    },
    
    viewTicket(ticketId) {
      this.$router.push(`/ticket/${ticketId}`)
    },
    
    showCreateUserDialog() {
      this.createUserVisible = true
    },
    
    showSystemStatus() {
      this.systemStatusVisible = true
    },
    
    async handleCreateUser() {
      try {
        this.createUserLoading = true
        
        const response = await axios.post('/api/auth/register', {
          username: this.createUserForm.username,
          email: this.createUserForm.email,
          firstName: this.createUserForm.firstName,
          lastName: this.createUserForm.lastName,
          password: this.createUserForm.password,
          role: this.createUserForm.role
        })
        
        if (response.data.success) {
          ElNotification({
            title: 'Success',
            message: 'User created successfully!',
            type: 'success'
          })
          
          this.createUserVisible = false
          this.resetCreateUserForm()
          await this.loadUsers()
        } else {
          ElMessage.error(response.data.message || 'Failed to create user')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || 'Failed to create user')
      } finally {
        this.createUserLoading = false
      }
    },
    
    resetCreateUserForm() {
      Object.keys(this.createUserForm).forEach(key => {
        this.createUserForm[key] = key === 'role' ? 'USER' : ''
      })
    },
    
    handleQuickAction(command) {
      switch (command) {
        case 'export':
          ElMessage.info('Export functionality coming soon!')
          break
        case 'import':
          ElMessage.info('Import functionality coming soon!')
          break
        case 'settings':
          this.$router.push('/settings')
          break
      }
    },
    
    onTimeFilterChange(value) {
      this.timeFilter = value
      // Reload data based on new time filter
      this.loadDashboardData()
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0;
  min-height: calc(100vh - 120px);
}

/* Enhanced Page Header */
.page-header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 32px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-text h2 {
  color: #1f2937;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-text p {
  color: #6b7280;
  font-size: 16px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.header-btn {
  height: 48px;
  padding: 0 24px;
  border-radius: 16px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.time-filters {
  display: flex;
  justify-content: center;
}

.time-filters :deep(.el-radio-button__inner) {
  border-radius: 12px;
  margin: 0 4px;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  font-weight: 500;
  transition: all 0.3s ease;
}

.time-filters :deep(.el-radio-button__inner:hover) {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
}

.time-filters :deep(.el-radio-button.is-active .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* Enhanced Statistics Cards */
.stats-row {
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, transparent 0%, var(--card-color) 50%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.stat-card:hover::before {
  opacity: 1;
}

.total-card {
  --card-color: #667eea;
}

.open-card {
  --card-color: #f56565;
}

.progress-card {
  --card-color: #4facfe;
}

.resolved-card {
  --card-color: #48bb78;
}

.stat-content {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.open {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
}

.stat-icon.progress {
  background: linear-gradient(135deg, #4facfe 0%, #00d4ff 100%);
}

.stat-icon.resolved {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.stat-details {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 4px;
  line-height: 1;
}

.stat-label {
  color: #6b7280;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  gap: 4px;
}

.stat-trend.positive {
  color: #10b981;
}

.stat-trend.negative {
  color: #ef4444;
}

.stat-progress {
  margin-top: 16px;
}

/* Performance Metrics */
.metrics-row {
  margin-bottom: 32px;
}

.metric-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: all 0.3s ease;
}

.metric-card:hover {
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.metric-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  gap: 8px;
}

.metric-icon {
  font-size: 24px;
}

.metric-icon.response {
  color: #8b5cf6;
}

.metric-icon.satisfaction {
  color: #f59e0b;
}

.metric-icon.resolution {
  color: #10b981;
}

.metric-title {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
}

.metric-value {
  font-size: 28px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 8px;
}

.metric-subtitle {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

/* Content Cards */
.content-row {
  margin-bottom: 32px;
}

.content-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  overflow: hidden;
}

.content-card :deep(.el-card__header) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  padding: 24px;
}

.content-card :deep(.el-card__body) {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-icon {
  font-size: 20px;
  color: #667eea;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.view-all-btn {
  height: 36px;
  padding: 0 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

/* Recent Tickets */
.tickets-list {
  max-height: 400px;
  overflow-y: auto;
}

.ticket-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 12px;
  background: rgba(248, 250, 252, 0.5);
  border: 1px solid rgba(226, 232, 240, 0.8);
  cursor: pointer;
  transition: all 0.3s ease;
}

.ticket-item:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(4px);
}

.ticket-item:last-child {
  margin-bottom: 0;
}

.ticket-info {
  flex: 1;
}

.ticket-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.ticket-id {
  font-size: 14px;
  font-weight: 700;
  color: #667eea;
}

.priority-tag {
  font-size: 12px;
}

.ticket-subject {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  line-height: 1.4;
}

.ticket-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ticket-date {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.ticket-actions .action-icon {
  color: #9ca3af;
  transition: all 0.3s ease;
}

.ticket-item:hover .action-icon {
  color: #667eea;
  transform: translateX(4px);
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #9ca3af;
}

.empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 24px;
}

/* User Management */
.user-management-content {
  padding: 0;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.user-stat-item {
  text-align: center;
  padding: 20px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.user-stat-number {
  font-size: 24px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 4px;
}

.user-stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.recent-users h4 {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
}

.users-list {
  margin-bottom: 32px;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  margin-bottom: 8px;
  background: rgba(248, 250, 252, 0.5);
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s ease;
}

.user-item:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: rgba(102, 126, 234, 0.2);
}

.user-avatar {
  margin-right: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 2px;
}

.user-email {
  font-size: 12px;
  color: #6b7280;
}

.quick-actions h4 {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.action-item:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.action-item .action-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.action-item .action-icon.create {
  color: #667eea;
}

.action-item .action-icon.view {
  color: #10b981;
}

.action-item .action-icon.report {
  color: #f59e0b;
}

.action-item .action-icon.status {
  color: #8b5cf6;
}

.action-item span {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
}

/* Dialog Styles */
.dialog-footer {
  text-align: right;
}

.system-status-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.status-section h4 {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
}

.status-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 12px;
  margin-bottom: 8px;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 12px;
}

.status-indicator.online {
  background: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.status-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  flex: 1;
}

.status-value {
  font-size: 14px;
  font-weight: 600;
  color: #10b981;
}

.metric-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 12px;
  margin-bottom: 8px;
}

.metric-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.metric-value {
  font-size: 14px;
  font-weight: 700;
  color: #667eea;
}

/* Responsive Design */
@media (max-width: 1200px) {
  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .stats-row .el-col {
    margin-bottom: 16px;
  }
  
  .metrics-row .el-col {
    margin-bottom: 16px;
  }
  
  .content-row .el-col {
    margin-bottom: 24px;
  }
  
  .user-stats {
    grid-template-columns: 1fr;
  }
  
  .ticket-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 640px) {
  .page-header {
    padding: 24px;
  }
  
  .header-text h2 {
    font-size: 24px;
  }
  
  .header-text p {
    font-size: 14px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .content-card :deep(.el-card__header),
  .content-card :deep(.el-card__body) {
    padding: 16px;
  }
  
  .ticket-item {
    padding: 12px;
  }
  
  .ticket-subject {
    font-size: 14px;
  }
}

/* Animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card,
.metric-card,
.content-card {
  animation: fadeInUp 0.6s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

/* Custom Scrollbar */
.tickets-list::-webkit-scrollbar {
  width: 6px;
}

.tickets-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.tickets-list::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.tickets-list::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}
</style>