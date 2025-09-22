<template>
  <div class="tickets-list">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-left">
        <h2>Support Tickets</h2>
        <p>Manage and track all support requests</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" size="large" @click="navigateToCreate" :icon="Plus">
          Create Ticket
        </el-button>
      </div>
    </div>

    <!-- Filters and Search -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-input
            v-model="searchQuery"
            placeholder="Search tickets..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="6" :md="4">
          <el-select
            v-model="statusFilter"
            placeholder="Status"
            clearable
            @change="handleFilter"
          >
            <el-option label="All Status" value="" />
            <el-option label="Open" value="Open" />
            <el-option label="In Progress" value="In Progress" />
            <el-option label="Resolved" value="Resolved" />
            <el-option label="Closed" value="Closed" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="6" :md="4">
          <el-select
            v-model="priorityFilter"
            placeholder="Priority"
            clearable
            @change="handleFilter"
          >
            <el-option label="All Priorities" value="" />
            <el-option label="High" value="High" />
            <el-option label="Medium" value="Medium" />
            <el-option label="Low" value="Low" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="4" :md="3">
          <el-button :icon="Refresh" @click="refreshTickets">Refresh</el-button>
        </el-col>
        <el-col :xs="24" :sm="24" :md="7" class="view-controls">
          <el-button-group>
            <el-button 
              :type="viewMode === 'table' ? 'primary' : ''" 
              :icon="List"
              @click="viewMode = 'table'"
            >
              Table
            </el-button>
            <el-button 
              :type="viewMode === 'cards' ? 'primary' : ''" 
              :icon="Grid"
              @click="viewMode = 'cards'"
            >
              Cards
            </el-button>
          </el-button-group>
        </el-col>
      </el-row>
    </el-card>

    <!-- Statistics Bar -->
    <el-row :gutter="16" class="stats-bar">
      <el-col :span="6">
        <div class="stat-item">
          <el-icon class="stat-icon total"><Tickets /></el-icon>
          <div class="stat-content">
            <span class="stat-number">{{ totalTickets }}</span>
            <span class="stat-label">Total</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-item">
          <el-icon class="stat-icon open"><Warning /></el-icon>
          <div class="stat-content">
            <span class="stat-number">{{ openTickets }}</span>
            <span class="stat-label">Open</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-item">
          <el-icon class="stat-icon progress"><Loading /></el-icon>
          <div class="stat-content">
            <span class="stat-number">{{ inProgressTickets }}</span>
            <span class="stat-label">In Progress</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-item">
          <el-icon class="stat-icon resolved"><CircleCheck /></el-icon>
          <div class="stat-content">
            <span class="stat-number">{{ resolvedTickets }}</span>
            <span class="stat-label">Resolved</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Table View -->
    <el-card v-if="viewMode === 'table'" class="table-card">
      <el-table
        :data="filteredTickets"
        style="width: 100%"
        v-loading="loading"
        :default-sort="{ prop: 'createdDate', order: 'descending' }"
        @selection-change="handleSelectionChange"
        @row-click="viewTicketDetails"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="subject" label="Subject" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <div class="subject-cell">
              <strong>{{ scope.row.subject }}</strong>
              <div class="subject-meta">
                <el-tag size="small" type="info">{{ scope.row.category }}</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="Priority" width="100" sortable>
          <template #default="scope">
            <el-tag
              :type="getPriorityType(scope.row.priority)"
              size="small"
            >
              {{ scope.row.priority }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" width="120" sortable>
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedTo" label="Assignee" width="120">
          <template #default="scope">
            <div v-if="scope.row.assignedTo" class="assignee-cell">
              <el-avatar :size="24" :src="getAvatarUrl(scope.row.assignedTo)" />
              <span>{{ scope.row.assignedTo }}</span>
            </div>
            <el-tag v-else type="info" size="small">Unassigned</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdDate" label="Created" width="120" sortable>
          <template #default="scope">
            {{ formatDate(scope.row.createdDate) }}
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="120" fixed="right">
          <template #default="scope">
            <el-dropdown>
              <el-button size="small" :icon="More" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="viewTicketDetails(scope.row)">
                    <el-icon><View /></el-icon>
                    View Details
                  </el-dropdown-item>
                  <el-dropdown-item @click="editTicket(scope.row)">
                    <el-icon><Edit /></el-icon>
                    Edit
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="deleteTicket(scope.row)">
                    <el-icon><Delete /></el-icon>
                    Delete
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalTickets"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- Cards View -->
    <div v-else class="cards-container">
      <el-row :gutter="20">
        <el-col
          v-for="ticket in filteredTickets"
          :key="ticket.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="ticket-card" @click="viewTicketDetails(ticket)">
            <div class="card-header">
              <div class="card-title">
                <h4>{{ ticket.subject }}</h4>
                <span class="ticket-id">#{{ ticket.id }}</span>
              </div>
              <el-dropdown>
                <el-icon class="card-menu"><More /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="viewTicketDetails(ticket)">View</el-dropdown-item>
                    <el-dropdown-item @click="editTicket(ticket)">Edit</el-dropdown-item>
                    <el-dropdown-item @click="deleteTicket(ticket)">Delete</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div class="card-content">
              <p class="description">{{ truncateText(ticket.description, 100) }}</p>
              <div class="card-tags">
                <el-tag :type="getPriorityType(ticket.priority)" size="small">
                  {{ ticket.priority }}
                </el-tag>
                <el-tag :type="getStatusType(ticket.status)" size="small">
                  {{ ticket.status }}
                </el-tag>
              </div>
            </div>
            <div class="card-footer">
              <div class="card-assignee">
                <el-avatar v-if="ticket.assignedTo" :size="20" :src="getAvatarUrl(ticket.assignedTo)" />
                <span>{{ ticket.assignedTo || 'Unassigned' }}</span>
              </div>
              <div class="card-date">
                {{ formatDate(ticket.createdDate) }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Ticket Details Dialog -->
    <el-dialog
      v-model="detailsDialogVisible"
      :title="`Ticket #${selectedTicket?.id} - ${selectedTicket?.subject}`"
      width="60%"
      top="5vh"
    >
      <div v-if="selectedTicket" class="ticket-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Subject">
            {{ selectedTicket.subject }}
          </el-descriptions-item>
          <el-descriptions-item label="Category">
            {{ selectedTicket.category }}
          </el-descriptions-item>
          <el-descriptions-item label="Priority">
            <el-tag :type="getPriorityType(selectedTicket.priority)">
              {{ selectedTicket.priority }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Status">
            <el-tag :type="getStatusType(selectedTicket.status)">
              {{ selectedTicket.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Assigned To">
            {{ selectedTicket.assignedTo || 'Unassigned' }}
          </el-descriptions-item>
          <el-descriptions-item label="Created Date">
            {{ formatFullDate(selectedTicket.createdDate) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="description-section">
          <h4>Description</h4>
          <div class="description-content">
            {{ selectedTicket.description }}
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailsDialogVisible = false">Close</el-button>
          <el-button type="primary" @click="editTicket(selectedTicket)">Edit Ticket</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import dayjs from 'dayjs'
import {
  Plus,
  Search,
  Refresh,
  List,
  Grid,
  More,
  View,
  Edit,
  Delete,
  Tickets,
  Warning,
  Loading,
  CircleCheck
} from '@element-plus/icons-vue'

export default {
  name: 'TicketsList',
  components: {
    // Icons are used directly in template, no need to register as components
  },
  data() {
    return {
      Plus,
      Search,
      Refresh,
      List,
      Grid,
      More,
      View,
      Edit,
      Delete,
      Tickets,
      Warning,
      Loading,
      CircleCheck,
      searchQuery: '',
      statusFilter: '',
      priorityFilter: '',
      viewMode: 'table',
      currentPage: 1,
      pageSize: 20,
      selectedTickets: [],
      detailsDialogVisible: false,
      selectedTicket: null
    }
  },
  computed: {
    ...mapState(['tickets', 'loading']),
    filteredTickets() {
      let filtered = [...this.tickets]
      
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(ticket =>
          ticket.subject.toLowerCase().includes(query) ||
          ticket.description.toLowerCase().includes(query) ||
          ticket.category.toLowerCase().includes(query)
        )
      }
      
      if (this.statusFilter) {
        filtered = filtered.filter(ticket => ticket.status === this.statusFilter)
      }
      
      if (this.priorityFilter) {
        filtered = filtered.filter(ticket => ticket.priority === this.priorityFilter)
      }
      
      return filtered
    },
    totalTickets() {
      return this.tickets.length
    },
    openTickets() {
      return this.tickets.filter(t => t.status === 'Open').length
    },
    inProgressTickets() {
      return this.tickets.filter(t => t.status === 'In Progress').length
    },
    resolvedTickets() {
      return this.tickets.filter(t => t.status === 'Resolved').length
    }
  },
  async mounted() {
    await this.fetchTickets()
  },
  methods: {
    ...mapActions(['fetchTickets']),
    handleSearch() {
      // Search is reactive through computed property
    },
    handleFilter() {
      // Filter is reactive through computed property
    },
    async refreshTickets() {
      await this.fetchTickets()
      this.$message.success('Tickets refreshed')
    },
    handleSelectionChange(selection) {
      this.selectedTickets = selection
    },
    handleSizeChange(size) {
      this.pageSize = size
    },
    handleCurrentChange(page) {
      this.currentPage = page
    },
    navigateToCreate() {
      this.$router.push('/file')
    },
    viewTicketDetails(ticket) {
      this.selectedTicket = ticket
      this.detailsDialogVisible = true
    },
    editTicket(ticket) {
      this.$router.push(`/tickets/${ticket.id}/edit`)
    },
    async deleteTicket(ticket) {
      try {
        await this.$confirm(
          `This will permanently delete ticket #${ticket.id}. Continue?`,
          'Warning',
          {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }
        )
        
        await this.$http.delete(`/api/tickets/${ticket.id}`)
        await this.fetchTickets()
        this.$message.success('Ticket deleted successfully')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('Failed to delete ticket')
        }
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
    getAvatarUrl(username) {
      return `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`
    },
    formatDate(date) {
      return dayjs(date).format('MMM DD, YYYY')
    },
    formatFullDate(date) {
      return dayjs(date).format('MMMM DD, YYYY HH:mm')
    },
    truncateText(text, length) {
      if (text.length <= length) return text
      return text.substring(0, length) + '...'
    }
  }
}
</script>

<style scoped>
.tickets-list {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.filter-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.view-controls {
  text-align: right;
}

.stats-bar {
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 24px;
  margin-right: 12px;
}

.stat-icon.total { color: #667eea; }
.stat-icon.open { color: #f56565; }
.stat-icon.progress { color: #4299e1; }
.stat-icon.resolved { color: #48bb78; }

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.subject-cell strong {
  font-weight: 600;
  color: #303133;
}

.subject-meta {
  margin-top: 4px;
}

.assignee-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.cards-container {
  margin-top: 20px;
}

.ticket-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  margin-bottom: 16px;
}

.ticket-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-title h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.ticket-id {
  font-size: 12px;
  color: #909399;
}

.card-menu {
  color: #c0c4cc;
  cursor: pointer;
}

.card-menu:hover {
  color: #409eff;
}

.card-content {
  margin-bottom: 16px;
}

.description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
}

.card-tags {
  display: flex;
  gap: 8px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.card-assignee {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.card-date {
  font-size: 12px;
  color: #c0c4cc;
}

.ticket-details {
  padding: 0;
}

.description-section {
  margin-top: 24px;
}

.description-section h4 {
  margin-bottom: 12px;
  color: #303133;
}

.description-content {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 6px;
  line-height: 1.6;
  color: #606266;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .el-button {
    width: 100%;
  }
  
  .view-controls {
    text-align: left;
    margin-top: 12px;
  }
  
  .stats-bar .el-col {
    margin-bottom: 12px;
  }
}
</style>
