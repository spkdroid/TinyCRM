<template>
  <div class="messaging-container">
    <!-- Header -->
    <div class="messaging-header">
      <h2>Messages</h2>
      <div class="header-actions">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="badge">
          <el-button size="small" @click="loadMessages">
            <el-icon><Refresh /></el-icon>
            Refresh
          </el-button>
        </el-badge>
        <el-button size="small" type="primary" @click="showComposeDialog = true">
          <el-icon><Plus /></el-icon>
          Compose
        </el-button>
      </div>
    </div>

    <!-- Messages List -->
    <div class="messages-content">
      <el-tabs v-model="activeTab" class="messages-tabs">
        <el-tab-pane label="Inbox" name="inbox">
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
                    <div class="sender-avatar">
                      <el-avatar :size="40" :src="message.sender?.avatarUrl">
                        <el-icon><User /></el-icon>
                      </el-avatar>
                    </div>
                    <div class="message-meta">
                      <h4 class="message-subject">{{ message.subject }}</h4>
                      <p class="message-sender">
                        From: {{ message.sender?.firstName }} {{ message.sender?.lastName }}
                      </p>
                      <p class="message-preview">{{ truncateMessage(message.message) }}</p>
                    </div>
                  </div>
                  <div class="message-actions">
                    <div class="message-priority" v-if="message.priority !== 'NORMAL'">
                      <el-tag 
                        :type="getPriorityType(message.priority)" 
                        size="small"
                      >
                        {{ message.priority }}
                      </el-tag>
                    </div>
                    <span class="message-time">{{ formatTime(message.createdAt) }}</span>
                    <el-dropdown @command="handleMessageAction">
                      <el-button size="small" text>
                        <el-icon><More /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item 
                            :command="{ action: 'reply', message: message }"
                          >
                            Reply
                          </el-dropdown-item>
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
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Sent" name="sent">
          <div class="message-list" v-loading="loadingSent">
            <div v-if="sentMessages.length === 0" class="empty-state">
              <el-empty description="No sent messages" />
            </div>
            
            <div v-else>
              <div 
                v-for="message in sentMessages" 
                :key="message.id"
                class="message-item sent-message"
                @click="openMessage(message)"
              >
                <div class="message-header">
                  <div class="message-info">
                    <div class="recipient-avatar">
                      <el-avatar :size="40" :src="message.recipient?.avatarUrl">
                        <el-icon><User /></el-icon>
                      </el-avatar>
                    </div>
                    <div class="message-meta">
                      <h4 class="message-subject">{{ message.subject }}</h4>
                      <p class="message-recipient">
                        To: {{ message.recipient?.firstName }} {{ message.recipient?.lastName }}
                      </p>
                      <p class="message-preview">{{ truncateMessage(message.message) }}</p>
                    </div>
                  </div>
                  <div class="message-actions">
                    <span class="message-time">{{ formatTime(message.createdAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- Compose Message Dialog -->
    <el-dialog
      v-model="showComposeDialog"
      title="Compose Message"
      width="600px"
    >
      <el-form :model="composeForm" label-width="100px">
        <el-form-item label="To" required>
          <el-select
            v-model="composeForm.recipientId"
            placeholder="Select recipient"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="user in availableUsers"
              :key="user.id"
              :label="`${user.firstName} ${user.lastName} (${user.username})`"
              :value="user.id"
            >
              <div class="user-option">
                <el-avatar :size="24" :src="user.avatarUrl">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="user-name">{{ user.firstName }} {{ user.lastName }}</span>
                <span class="user-username">@{{ user.username }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="Priority">
          <el-select v-model="composeForm.priority" placeholder="Select priority">
            <el-option label="Low" value="LOW" />
            <el-option label="Normal" value="NORMAL" />
            <el-option label="High" value="HIGH" />
            <el-option label="Urgent" value="URGENT" />
          </el-select>
        </el-form-item>

        <el-form-item label="Subject" required>
          <el-input v-model="composeForm.subject" placeholder="Enter subject" />
        </el-form-item>

        <el-form-item label="Message" required>
          <el-input
            v-model="composeForm.message"
            type="textarea"
            :rows="6"
            placeholder="Type your message here..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showComposeDialog = false">Cancel</el-button>
          <el-button 
            type="primary" 
            @click="sendMessage"
            :loading="sending"
            :disabled="!composeForm.recipientId || !composeForm.subject || !composeForm.message"
          >
            Send Message
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Message Detail Dialog -->
    <el-dialog
      v-model="showMessageDialog"
      :title="selectedMessage?.subject"
      width="700px"
    >
      <div v-if="selectedMessage" class="message-detail">
        <div class="message-meta-detail">
          <div class="participant-info">
            <el-avatar :size="50" :src="selectedMessage.sender?.avatarUrl">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="participant-details">
              <strong>{{ selectedMessage.sender?.firstName }} {{ selectedMessage.sender?.lastName }}</strong>
              <span class="participant-username">@{{ selectedMessage.sender?.username }}</span>
              <div class="message-time-detail">{{ formatFullTime(selectedMessage.createdAt) }}</div>
            </div>
          </div>
          <div class="message-priority-detail" v-if="selectedMessage.priority !== 'NORMAL'">
            <el-tag :type="getPriorityType(selectedMessage.priority)">
              {{ selectedMessage.priority }} Priority
            </el-tag>
          </div>
        </div>
        
        <div class="message-content">
          <p>{{ selectedMessage.message }}</p>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showMessageDialog = false">Close</el-button>
          <el-button 
            v-if="selectedMessage && !selectedMessage.read && activeTab === 'inbox'" 
            type="primary" 
            @click="markMessageAsRead(selectedMessage.id)"
          >
            Mark as Read
          </el-button>
          <el-button 
            v-if="selectedMessage" 
            @click="replyToMessage(selectedMessage)"
          >
            Reply
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
  Refresh, Plus, User, More 
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'UserMessaging',
  components: {
    Refresh, Plus, User, More
  },
  setup() {
    const loading = ref(false)
    const loadingSent = ref(false)
    const sending = ref(false)
    const messages = ref([])
    const sentMessages = ref([])
    const availableUsers = ref([])
    const selectedMessage = ref(null)
    const showMessageDialog = ref(false)
    const showComposeDialog = ref(false)
    const activeTab = ref('inbox')

    const composeForm = ref({
      recipientId: null,
      subject: '',
      message: '',
      priority: 'NORMAL'
    })

    const unreadCount = computed(() => {
      return messages.value.filter(msg => !msg.read).length
    })

    const loadMessages = async () => {
      try {
        loading.value = true
        const response = await axios.get('/api/messages')
        
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

    const loadSentMessages = async () => {
      try {
        loadingSent.value = true
        const response = await axios.get('/api/messages/sent')
        
        if (response.data.success) {
          sentMessages.value = response.data.messages
        } else {
          ElMessage.error(response.data.message || 'Failed to load sent messages')
        }
      } catch (error) {
        console.error('Load sent messages error:', error)
        ElMessage.error('Failed to load sent messages')
      } finally {
        loadingSent.value = false
      }
    }

    const loadAvailableUsers = async () => {
      try {
        const response = await axios.get('/api/messages/users')
        
        if (response.data.success) {
          availableUsers.value = response.data.users
        } else {
          ElMessage.error(response.data.message || 'Failed to load users')
        }
      } catch (error) {
        console.error('Load users error:', error)
        ElMessage.error('Failed to load users')
      }
    }

    const sendMessage = async () => {
      try {
        sending.value = true
        const response = await axios.post('/api/messages', composeForm.value)
        
        if (response.data.success) {
          ElMessage.success('Message sent successfully')
          showComposeDialog.value = false
          composeForm.value = {
            recipientId: null,
            subject: '',
            message: '',
            priority: 'NORMAL'
          }
          loadSentMessages()
        } else {
          ElMessage.error(response.data.message || 'Failed to send message')
        }
      } catch (error) {
        console.error('Send message error:', error)
        ElMessage.error('Failed to send message')
      } finally {
        sending.value = false
      }
    }

    const openMessage = async (message) => {
      selectedMessage.value = message
      showMessageDialog.value = true
      
      // Mark as read if unread and in inbox
      if (!message.read && activeTab.value === 'inbox') {
        await markMessageAsRead(message.id)
      }
    }

    const markMessageAsRead = async (messageId) => {
      try {
        const response = await axios.put(`/api/messages/${messageId}/read`)
        
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
        const response = await axios.delete(`/api/messages/${messageId}`)
        
        if (response.data.success) {
          messages.value = messages.value.filter(msg => msg.id !== messageId)
          sentMessages.value = sentMessages.value.filter(msg => msg.id !== messageId)
          ElMessage.success('Message deleted')
        } else {
          ElMessage.error(response.data.message || 'Failed to delete message')
        }
      } catch (error) {
        console.error('Delete message error:', error)
        ElMessage.error('Failed to delete message')
      }
    }

    const replyToMessage = (message) => {
      composeForm.value = {
        recipientId: message.sender.id,
        subject: message.subject.startsWith('Re: ') ? message.subject : `Re: ${message.subject}`,
        message: '',
        priority: 'NORMAL'
      }
      showMessageDialog.value = false
      showComposeDialog.value = true
    }

    const handleMessageAction = async ({ action, messageId, message }) => {
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
      } else if (action === 'reply') {
        replyToMessage(message)
      }
    }

    const getPriorityType = (priority) => {
      const priorityTypes = {
        'LOW': 'info',
        'NORMAL': 'primary',
        'HIGH': 'warning',
        'URGENT': 'danger'
      }
      return priorityTypes[priority] || 'primary'
    }

    const truncateMessage = (message) => {
      if (!message) return ''
      return message.length > 80 ? message.substring(0, 80) + '...' : message
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
      loadSentMessages()
      loadAvailableUsers()
    })

    return {
      loading,
      loadingSent,
      sending,
      messages,
      sentMessages,
      availableUsers,
      selectedMessage,
      showMessageDialog,
      showComposeDialog,
      activeTab,
      composeForm,
      unreadCount,
      loadMessages,
      loadSentMessages,
      sendMessage,
      openMessage,
      markMessageAsRead,
      deleteMessage,
      replyToMessage,
      handleMessageAction,
      getPriorityType,
      truncateMessage,
      formatTime,
      formatFullTime
    }
  }
}
</script>

<style scoped>
.messaging-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.messaging-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.messaging-header h2 {
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

.messages-content {
  min-height: 500px;
}

.messages-tabs {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.message-list {
  min-height: 400px;
  padding: 0;
}

.message-item {
  border-bottom: 1px solid #ebeef5;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.message-item:hover {
  background-color: #f8f9fa;
}

.message-item.unread {
  background-color: #f0f9ff;
  border-left: 4px solid #409EFF;
}

.message-item.sent-message {
  background-color: #fafafa;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.message-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
}

.sender-avatar,
.recipient-avatar {
  flex-shrink: 0;
}

.message-meta {
  flex: 1;
}

.message-subject {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.message-sender,
.message-recipient {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #909399;
}

.message-preview {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
}

.message-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.message-priority {
  margin-bottom: 5px;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.user-option {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-weight: 500;
}

.user-username {
  font-size: 12px;
  color: #909399;
}

.message-detail {
  padding: 10px 0;
}

.message-meta-detail {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.participant-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.participant-details strong {
  display: block;
  color: #303133;
}

.participant-username {
  font-size: 12px;
  color: #909399;
}

.message-time-detail {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.message-content {
  margin-bottom: 20px;
}

.message-content p {
  line-height: 1.6;
  color: #606266;
  margin: 0;
  white-space: pre-wrap;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}
</style>