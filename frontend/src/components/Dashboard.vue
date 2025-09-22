<template>
  <div class="dashboard">
    <!-- Page Header -->
    <div class="page-header">
      <h2>Dashboard Overview</h2>
      <p>Welcome to TinyCRM Enterprise - Manage your support tickets efficiently</p>
    </div>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon size="24"><Tickets /></el-icon>
            </div>
            <div class="stat-details">
              <h3>{{ totalTickets }}</h3>
              <p>Total Tickets</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon open">
              <el-icon size="24"><Warning /></el-icon>
            </div>
            <div class="stat-details">
              <h3>{{ openTickets }}</h3>
              <p>Open Tickets</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon progress">
              <el-icon size="24"><Loading /></el-icon>
            </div>
            <div class="stat-details">
              <h3>{{ inProgressTickets }}</h3>
              <p>In Progress</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon resolved">
              <el-icon size="24"><CircleCheck /></el-icon>
            </div>
            <div class="stat-details">
              <h3>{{ resolvedTickets }}</h3>
              <p>Resolved</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts and Recent Activity -->
    <el-row :gutter="20" class="content-row">
      <!-- Recent Tickets -->
      <el-col :xs="24" :lg="16">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>Recent Tickets</span>
              <el-button type="primary" size="small" @click="navigateToTickets">
                View All
              </el-button>
            </div>
          </template>
          <el-table
            :data="recentTickets"
            style="width: 100%"
            v-loading="loading"
          >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="subject" label="Subject" />
            <el-table-column prop="priority" label="Priority" width="100">
              <template #default="scope">
                <el-tag
                  :type="getPriorityType(scope.row.priority)"
                  size="small"
                >
                  {{ scope.row.priority }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="Status" width="120">
              <template #default="scope">
                <el-tag
                  :type="getStatusType(scope.row.status)"
                  size="small"
                >
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdDate" label="Created" width="120">
              <template #default="scope">
                {{ formatDate(scope.row.createdDate) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- Quick Actions -->
      <el-col :xs="24" :lg="8">
        <el-card class="content-card">
          <template #header>
            <span>Quick Actions</span>
          </template>
          <div class="quick-actions">
            <el-button
              type="primary"
              class="action-button"
              @click="navigateToCreateTicket"
            >
              <el-icon><DocumentAdd /></el-icon>
              Create New Ticket
            </el-button>
            <el-button
              type="success"
              class="action-button"
              @click="navigateToTickets"
            >
              <el-icon><View /></el-icon>
              View All Tickets
            </el-button>
            <el-button
              type="info"
              class="action-button"
              @click="navigateToReports"
            >
              <el-icon><DataAnalysis /></el-icon>
              Generate Report
            </el-button>
          </div>
        </el-card>

        <!-- System Status -->
        <el-card class="content-card mt-20">
          <template #header>
            <span>System Status</span>
          </template>
          <div class="system-status">
            <div class="status-item">
              <el-icon class="status-icon online"><CircleCheck /></el-icon>
              <span>Database: Online</span>
            </div>
            <div class="status-item">
              <el-icon class="status-icon online"><CircleCheck /></el-icon>
              <span>API Server: Online</span>
            </div>
            <div class="status-item">
              <el-icon class="status-icon online"><CircleCheck /></el-icon>
              <span>Email Service: Online</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import dayjs from 'dayjs'

export default {
  name: 'DashboardView',
  data() {
    return {
      totalTickets: 0,
      openTickets: 0,
      inProgressTickets: 0,
      resolvedTickets: 0,
      recentTickets: []
    }
  },
  computed: {
    ...mapState(['tickets', 'loading'])
  },
  async mounted() {
    await this.loadDashboardData()
  },
  methods: {
    ...mapActions(['fetchTickets']),
    async loadDashboardData() {
      try {
        await this.fetchTickets()
        this.calculateStats()
        this.recentTickets = this.tickets.slice(0, 5)
      } catch (error) {
        this.$message.error('Failed to load dashboard data')
      }
    },
    calculateStats() {
      this.totalTickets = this.tickets.length
      this.openTickets = this.tickets.filter(t => t.status === 'Open').length
      this.inProgressTickets = this.tickets.filter(t => t.status === 'In Progress').length
      this.resolvedTickets = this.tickets.filter(t => t.status === 'Resolved').length
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
    navigateToCreateTicket() {
      this.$router.push('/file')
    },
    navigateToReports() {
      this.$router.push('/reports/overview')
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  color: #303133;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}

.page-header p {
  color: #909399;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.open {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.progress {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.resolved {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-details h3 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-details p {
  color: #909399;
  font-size: 14px;
}

.content-row {
  margin-bottom: 24px;
}

.content-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-button {
  width: 100%;
  height: 48px;
  font-size: 14px;
  border-radius: 6px;
}

.system-status {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.status-icon {
  margin-right: 8px;
}

.status-icon.online {
  color: #67c23a;
}

.mt-20 {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: 12px;
  }
  
  .content-row .el-col {
    margin-bottom: 20px;
  }
}
</style>
