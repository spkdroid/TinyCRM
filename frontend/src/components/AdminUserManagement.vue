<template>
  <div class="admin-users-container">
    <!-- Header -->
    <div class="admin-header">
      <h2>User Management</h2>
      <div class="header-actions">
        <el-button size="small" @click="loadUsers" :loading="loading">
          <el-icon><Refresh /></el-icon>
          Refresh
        </el-button>
      </div>
    </div>

    <!-- Users Table -->
    <el-card>
      <el-table
        :data="users"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="Avatar" width="80">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatarUrl">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column label="Name" min-width="150">
          <template #default="scope">
            <div class="user-info">
              <strong>{{ scope.row.firstName }} {{ scope.row.lastName }}</strong>
              <div class="username">@{{ scope.row.username }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="Email" min-width="200" />

        <el-table-column label="Role" width="120">
          <template #default="scope">
            <el-tag 
              :type="getRoleType(scope.row.role)" 
              size="small"
            >
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Status" width="100">
          <template #default="scope">
            <el-tag 
              :type="scope.row.active ? 'success' : 'danger'" 
              size="small"
            >
              {{ scope.row.active ? 'Active' : 'Inactive' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Created" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="Last Login" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.lastLoginAt) || 'Never' }}
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="200" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                size="small" 
                @click="viewUserProfile(scope.row)"
              >
                <el-icon><View /></el-icon>
                View
              </el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click="confirmDeleteUser(scope.row)"
                :disabled="scope.row.role === 'ADMIN' && scope.row.username === currentUsername"
              >
                <el-icon><Delete /></el-icon>
                Delete
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container" v-if="users.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalUsers"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- User Profile Dialog -->
    <el-dialog
      v-model="showUserDialog"
      :title="`User Profile - ${selectedUser?.firstName} ${selectedUser?.lastName}`"
      width="600px"
    >
      <div v-if="selectedUser" class="user-profile-detail">
        <div class="profile-header-detail">
          <el-avatar :size="80" :src="selectedUser.avatarUrl">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="profile-info">
            <h3>{{ selectedUser.firstName }} {{ selectedUser.lastName }}</h3>
            <p>@{{ selectedUser.username }}</p>
            <p>{{ selectedUser.email }}</p>
            <el-tag :type="getRoleType(selectedUser.role)">{{ selectedUser.role }}</el-tag>
          </div>
        </div>

        <el-divider />

        <el-descriptions :column="2" border>
          <el-descriptions-item label="Phone">
            {{ selectedUser.phoneNumber || 'Not provided' }}
          </el-descriptions-item>
          <el-descriptions-item label="Job Title">
            {{ selectedUser.jobTitle || 'Not provided' }}
          </el-descriptions-item>
          <el-descriptions-item label="Department">
            {{ selectedUser.department || 'Not provided' }}
          </el-descriptions-item>
          <el-descriptions-item label="Company">
            {{ selectedUser.company || 'Not provided' }}
          </el-descriptions-item>
          <el-descriptions-item label="Address" :span="2">
            {{ formatAddress(selectedUser) || 'Not provided' }}
          </el-descriptions-item>
          <el-descriptions-item label="Bio" :span="2">
            {{ selectedUser.bio || 'Not provided' }}
          </el-descriptions-item>
          <el-descriptions-item label="Created">
            {{ formatDate(selectedUser.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="Last Login">
            {{ formatDate(selectedUser.lastLoginAt) || 'Never' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUserDialog = false">Close</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Delete Confirmation Dialog -->
    <el-dialog
      v-model="showDeleteDialog"
      title="Confirm User Deletion"
      width="500px"
    >
      <div v-if="userToDelete" class="delete-confirmation">
        <el-alert
          title="Warning"
          type="warning"
          description="This action cannot be undone. All user data including profile information and associated records will be permanently deleted."
          show-icon
          :closable="false"
        />
        
        <div class="user-delete-info">
          <p><strong>You are about to delete:</strong></p>
          <div class="user-summary">
            <el-avatar :size="50" :src="userToDelete.avatarUrl">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="user-details-delete">
              <h4>{{ userToDelete.firstName }} {{ userToDelete.lastName }}</h4>
              <p>@{{ userToDelete.username }}</p>
              <p>{{ userToDelete.email }}</p>
              <el-tag :type="getRoleType(userToDelete.role)" size="small">
                {{ userToDelete.role }}
              </el-tag>
            </div>
          </div>
        </div>

        <p class="confirmation-text">
          Type <strong>DELETE</strong> to confirm:
        </p>
        <el-input
          v-model="deleteConfirmation"
          placeholder="Type DELETE to confirm"
          @keyup.enter="deleteUser"
        />
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDeleteDialog = false">Cancel</el-button>
          <el-button 
            type="danger" 
            @click="deleteUser"
            :disabled="deleteConfirmation !== 'DELETE'"
            :loading="deleting"
          >
            Delete User
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, User, View, Delete 
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'AdminUserManagement',
  components: {
    Refresh, User, View, Delete
  },
  setup() {
    const loading = ref(false)
    const deleting = ref(false)
    const users = ref([])
    const selectedUser = ref(null)
    const userToDelete = ref(null)
    const showUserDialog = ref(false)
    const showDeleteDialog = ref(false)
    const deleteConfirmation = ref('')
    const currentUsername = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalUsers = ref(0)

    const loadUsers = async () => {
      try {
        loading.value = true
        const response = await axios.get('/api/users/admin/all')
        
        if (response.data.success) {
          users.value = response.data.users
          totalUsers.value = response.data.totalUsers
        } else {
          ElMessage.error(response.data.message || 'Failed to load users')
        }
      } catch (error) {
        console.error('Load users error:', error)
        if (error.response?.status === 403) {
          ElMessage.error('Access denied. Admin privileges required.')
        } else {
          ElMessage.error('Failed to load users')
        }
      } finally {
        loading.value = false
      }
    }

    const viewUserProfile = (user) => {
      selectedUser.value = user
      showUserDialog.value = true
    }

    const confirmDeleteUser = (user) => {
      userToDelete.value = user
      deleteConfirmation.value = ''
      showDeleteDialog.value = true
    }

    const deleteUser = async () => {
      if (deleteConfirmation.value !== 'DELETE') {
        ElMessage.warning('Please type DELETE to confirm')
        return
      }

      try {
        deleting.value = true
        const response = await axios.delete(`/api/users/${userToDelete.value.id}`)
        
        if (response.data.success) {
          ElMessage.success('User deleted successfully')
          users.value = users.value.filter(u => u.id !== userToDelete.value.id)
          totalUsers.value--
          showDeleteDialog.value = false
          userToDelete.value = null
          deleteConfirmation.value = ''
        } else {
          ElMessage.error(response.data.message || 'Failed to delete user')
        }
      } catch (error) {
        console.error('Delete user error:', error)
        if (error.response?.status === 403) {
          ElMessage.error('Access denied. Admin privileges required.')
        } else if (error.response?.status === 400) {
          ElMessage.error(error.response.data.message || 'Cannot delete user')
        } else {
          ElMessage.error('Failed to delete user')
        }
      } finally {
        deleting.value = false
      }
    }

    const handlePageChange = (page) => {
      currentPage.value = page
      // In a real app, you'd implement server-side pagination
      // loadUsers(page)
    }

    const getRoleType = (role) => {
      const roleTypes = {
        'ADMIN': 'danger',
        'SUPPORT': 'warning',
        'USER': 'primary'
      }
      return roleTypes[role] || 'info'
    }

    const formatDate = (dateString) => {
      if (!dateString) return null
      const date = new Date(dateString)
      return date.toLocaleDateString()
    }

    const formatAddress = (user) => {
      const addressParts = [
        user.address,
        user.city,
        user.state,
        user.zipCode,
        user.country
      ].filter(part => part && part.trim())
      
      return addressParts.length > 0 ? addressParts.join(', ') : null
    }

    // Get current user info
    const getCurrentUser = async () => {
      try {
        const response = await axios.get('/api/profile')
        if (response.data.success) {
          currentUsername.value = response.data.profile.username
        }
      } catch (error) {
        console.error('Error getting current user:', error)
      }
    }

    onMounted(() => {
      getCurrentUser()
      loadUsers()
    })

    return {
      loading,
      deleting,
      users,
      selectedUser,
      userToDelete,
      showUserDialog,
      showDeleteDialog,
      deleteConfirmation,
      currentUsername,
      currentPage,
      pageSize,
      totalUsers,
      loadUsers,
      viewUserProfile,
      confirmDeleteUser,
      deleteUser,
      handlePageChange,
      getRoleType,
      formatDate,
      formatAddress
    }
  }
}
</script>

<style scoped>
.admin-users-container {
  padding: 20px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.admin-header h2 {
  margin: 0;
  color: #303133;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.user-profile-detail {
  padding: 10px 0;
}

.profile-header-detail {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.profile-info h3 {
  margin: 0 0 5px 0;
  color: #303133;
}

.profile-info p {
  margin: 5px 0;
  color: #606266;
}

.delete-confirmation {
  padding: 10px 0;
}

.user-delete-info {
  margin: 20px 0;
}

.user-summary {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-top: 10px;
}

.user-details-delete h4 {
  margin: 0 0 5px 0;
  color: #303133;
}

.user-details-delete p {
  margin: 2px 0;
  color: #606266;
  font-size: 14px;
}

.confirmation-text {
  margin: 20px 0 10px 0;
  font-weight: bold;
}
</style>