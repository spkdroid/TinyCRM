<template>
  <div class="inbox-container">
    <!-- Header -->
    <div class="inbox-header">
      <h2>Inbox</h2>
      <div class="header-actions">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="badge">
          <el-button size="small" @click="loadMessages">
            <el-icon><Refresh /></el-icon>
            Refresh
          </el-button>
        </el-badge>
        <el-button size="small" @click="markAllAsRead" :disabled="unreadCount === 0">
          <el-icon><Check /></el-icon>
          Mark All Read
        </el-button>
      </div>
    </div>

    <!-- Message List -->
    <div class="message-list" v-loading="loading">
      <div v-if="messages.length === 0" class="empty-state">
        <el-empty description="No messages in your inbox" />
      </div>
      
      <div v-else>
        <div 
          v-for="message in messages" 
          :key="message.id"
          class="message-item"
          :class="{ 'unread': !message.read, 'read': message.read }"
          @click="openMessage(message)"
        >
          <div class="message-header">
            <div class="message-info">
              <div class="message-type-icon">
                <el-icon v-if="message.type === 'COMMENT'" color="#409EFF">
                  <ChatDotRound />
                </el-icon>
                <el-icon v-else-if="message.type === 'TICKET_UPDATE'" color="#67C23A">
                  <DocumentCopy />
                </el-icon>
                <el-icon v-else-if="message.type === 'ADMIN_MESSAGE'" color="#E6A23C">
                  <Warning />
                </el-icon>
                <el-icon v-else color="#909399">
                  <Bell />
                </el-icon>
              </div>
              <div class="message-meta">
                <h4 class="message-title">{{ message.title }}</h4>
                <p class="message-sender" v-if="message.sender">
                  From: {{ message.sender.firstName }} {{ message.sender.lastName }}
                </p>
                <p class="message-sender" v-else>
                  System Message
                </p>
              </div>
            </div>
            <div class="message-actions">
              <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              <el-dropdown @command="handleMessageAction">
                <el-button size="small" text>
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item 
                      :command="{ action: 'read', messageId: message.id }"
                      v-if="!message.read"
                    >
                      Mark as Read
                    </el-dropdown-item>
                    <el-dropdown-item 
                      :command="{ action: 'delete', messageId: message.id }"
                      divided
                    >
                      Delete
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          <div class="message-preview">
            <p>{{ truncateMessage(message.message) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Message Detail Dialog -->
    <el-dialog
      v-model="showMessageDialog"
      :title="selectedMessage?.title"
      width="600px"
    >
      <div v-if="selectedMessage" class="message-detail">
        <div class="message-meta-detail">
          <div class="sender-info" v-if="selectedMessage.sender">
            <el-avatar :size="40" :src="selectedMessage.sender.avatarUrl">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="sender-details">
              <strong>{{ selectedMessage.sender.firstName }} {{ selectedMessage.sender.lastName }}</strong>
              <span class="sender-username">@{{ selectedMessage.sender.username }}</span>
            </div>
          </div>
          <div class="sender-info" v-else>
            <el-avatar :size="40">
              <el-icon><Setting /></el-icon>
            </el-avatar>
            <div class="sender-details">
              <strong>System</strong>
              <span class="sender-username">Automated Message</span>
            </div>
          </div>
          <div class="message-time-detail">
            {{ formatFullTime(selectedMessage.createdAt) }}
          </div>
        </div>
        
        <div class="message-content">
          <p>{{ selectedMessage.message }}</p>
        </div>

        <!-- Related Ticket/Comment Links -->
        <div v-if="selectedMessage.relatedTicket" class="related-content">
          <el-divider>Related Content</el-divider>
          <el-button 
            type="primary" 
            size="small" 
            @click="openTicket(selectedMessage.relatedTicket.id)"
          >
            <el-icon><Link /></el-icon>
            View Ticket #{{ selectedMessage.relatedTicket.id }}
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showMessageDialog = false">Close</el-button>
          <el-button 
            v-if="selectedMessage && !selectedMessage.read" 
            type="primary" 
            @click="markMessageAsRead(selectedMessage.id)"
          >
            Mark as Read
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, Check, ChatDotRound, DocumentCopy, Warning, Bell, More, 
  User, Setting, Link 
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'Inbox',
  components: {
    Refresh, Check, ChatDotRound, DocumentCopy, Warning, Bell, More,
    User, Setting, Link
  },
  setup() {
    const loading = ref(false)
    const messages = ref([])
    const selectedMessage = ref(null)
    const showMessageDialog = ref(false)

    const unreadCount = computed(() => {
      return messages.value.filter(msg => !msg.read).length
    })

    const loadMessages = async () => {
      try {
        loading.value = true
        const response = await axios.get('/api/inbox')
        
        if (response.data.success) {
          messages.value = response.data.messages
        } else {
          ElMessage.error(response.data.message || 'Failed to load messages')
        }
      } catch (error) {
        console.error('Load messages error:', error)
        ElMessage.error('Failed to load messages')
      } finally {
        loading.value = false
      }
    }

    const markAllAsRead = async () => {
      try {
        const response = await axios.put('/api/inbox/read-all')
        
        if (response.data.success) {
          messages.value.forEach(msg => {
            msg.read = true
            msg.readAt = new Date().toISOString()
          })
          ElMessage.success('All messages marked as read')
        } else {
          ElMessage.error(response.data.message || 'Failed to mark messages as read')
        }
      } catch (error) {
        console.error('Mark all read error:', error)
        ElMessage.error('Failed to mark messages as read')
      }
    }

    const markMessageAsRead = async (messageId) => {
      try {
        const response = await axios.put(`/api/inbox/${messageId}/read`)
        
        if (response.data.success) {
          const message = messages.value.find(msg => msg.id === messageId)
          if (message) {
            message.read = true
            message.readAt = new Date().toISOString()
          }
          if (selectedMessage.value && selectedMessage.value.id === messageId) {
            selectedMessage.value.read = true
            selectedMessage.value.readAt = new Date().toISOString()
          }
          ElMessage.success('Message marked as read')
        } else {
          ElMessage.error(response.data.message || 'Failed to mark message as read')
        }
      } catch (error) {
        console.error('Mark as read error:', error)
        ElMessage.error('Failed to mark message as read')
      }
    }

    const deleteMessage = async (messageId) => {
      try {
        const response = await axios.delete(`/api/inbox/${messageId}`)
        
        if (response.data.success) {
          messages.value = messages.value.filter(msg => msg.id !== messageId)
          ElMessage.success('Message deleted')
        } else {
          ElMessage.error(response.data.message || 'Failed to delete message')
        }
      } catch (error) {
        console.error('Delete message error:', error)
        ElMessage.error('Failed to delete message')
      }
    }

    const openMessage = async (message) => {
      selectedMessage.value = message
      showMessageDialog.value = true
      
      // Mark as read if unread
      if (!message.read) {
        await markMessageAsRead(message.id)
      }
    }

    const handleMessageAction = async ({ action, messageId }) => {
      if (action === 'read') {
        await markMessageAsRead(messageId)
      } else if (action === 'delete') {
        try {
          await ElMessageBox.confirm(
            'This will permanently delete the message. Continue?',
            'Warning',
            {
              confirmButtonText: 'Delete',
              cancelButtonText: 'Cancel',
              type: 'warning',
            }
          )
          await deleteMessage(messageId)
        } catch {
          // User cancelled
        }
      }
    }

    const openTicket = (ticketId) => {
      // Navigate to ticket detail - this would need router integration
      ElMessage.info(`Opening ticket #${ticketId}`)
      // In a real app: this.$router.push(`/tickets/${ticketId}`)
    }

    const truncateMessage = (message) => {
      if (!message) return ''
      return message.length > 100 ? message.substring(0, 100) + '...' : message
    }

    const formatTime = (dateString) => {
      const date = new Date(dateString)
      const now = new Date()
      const diffTime = Math.abs(now - date)
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      
      if (diffDays === 1) {
        return 'Today'
      } else if (diffDays === 2) {
        return 'Yesterday'
      } else if (diffDays < 7) {
        return `${diffDays - 1} days ago`
      } else {
        return date.toLocaleDateString()
      }
    }

    const formatFullTime = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    onMounted(() => {
      loadMessages()
    })

    return {
      loading,
      messages,
      selectedMessage,
      showMessageDialog,
      unreadCount,
      loadMessages,
      markAllAsRead,
      markMessageAsRead,
      deleteMessage,
      openMessage,
      handleMessageAction,
      openTicket,
      truncateMessage,
      formatTime,
      formatFullTime
    }
  }
}
</script>

<style scoped>
.inbox-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.inbox-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.inbox-header h2 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.badge {
  margin-right: 10px;
}

.message-list {
  min-height: 400px;
}

.message-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 10px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.message-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background-color: #f0f9ff;
  border-left: 4px solid #409EFF;
}

.message-item.read {
  background-color: #ffffff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.message-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-type-icon {
  margin-top: 2px;
}

.message-meta h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.message-sender {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.message-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-preview p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
}

.message-detail {
  padding: 10px 0;
}

.message-meta-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.sender-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sender-details strong {
  display: block;
  color: #303133;
}

.sender-username {
  font-size: 12px;
  color: #909399;
}

.message-time-detail {
  font-size: 14px;
  color: #909399;
}

.message-content {
  margin-bottom: 20px;
}

.message-content p {
  line-height: 1.6;
  color: #606266;
  margin: 0;
}

.related-content {
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}
</style>