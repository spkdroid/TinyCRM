<template>
  <div class="ticket-comments-container">
    <!-- Ticket Information -->
    <div v-if="ticket" class="ticket-info">
      <el-card class="ticket-card">
        <template #header>
          <div class="ticket-header">
            <h3>Ticket #{{ ticket.id }} - {{ ticket.subject }}</h3>
            <el-tag 
              :type="getStatusType(ticket.status)" 
              size="large"
            >
              {{ ticket.status }}
            </el-tag>
          </div>
        </template>
        <div class="ticket-details">
          <el-row :gutter="20">
            <el-col :span="12">
              <p><strong>Priority:</strong> 
                <el-tag 
                  :type="getPriorityType(ticket.priority)" 
                  size="small"
                >
                  {{ ticket.priority }}
                </el-tag>
              </p>
              <p><strong>Category:</strong> {{ ticket.category }}</p>
              <p><strong>Created By:</strong> {{ ticket.createdBy }}</p>
            </el-col>
            <el-col :span="12">
              <p><strong>Assigned To:</strong> {{ ticket.assignedTo || 'Unassigned' }}</p>
              <p><strong>Created:</strong> {{ formatDate(ticket.createdDate) }}</p>
              <p><strong>Updated:</strong> {{ formatDate(ticket.updatedDate) }}</p>
            </el-col>
          </el-row>
          <div class="ticket-description">
            <strong>Description:</strong>
            <p>{{ ticket.description }}</p>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Comments Section -->
    <div class="comments-section">
      <el-card>
        <template #header>
          <div class="comments-header">
            <h3>Comments ({{ comments.length }})</h3>
            <el-button 
              size="small" 
              @click="loadComments"
              :loading="loadingComments"
            >
              <el-icon><Refresh /></el-icon>
              Refresh
            </el-button>
          </div>
        </template>

        <!-- Comments List -->
        <div class="comments-list" v-loading="loadingComments">
          <div v-if="comments.length === 0" class="no-comments">
            <el-empty description="No comments yet. Be the first to comment!" />
          </div>
          
          <div v-else>
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-header">
                <div class="comment-author">
                  <el-avatar :size="32">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div class="author-info">
                    <strong>{{ comment.author }}</strong>
                    <span class="comment-time">{{ formatDate(comment.createdDate) }}</span>
                  </div>
                </div>
              </div>
              <div class="comment-content">
                <p>{{ comment.comment }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Add Comment Form -->
        <div class="add-comment-section">
          <el-divider>Add Comment</el-divider>
          <el-form @submit.prevent="addComment">
            <el-form-item>
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="4"
                placeholder="Type your comment here..."
                :disabled="submittingComment"
              />
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                @click="addComment"
                :loading="submittingComment"
                :disabled="!newComment.trim()"
              >
                <el-icon><ChatDotRound /></el-icon>
                Add Comment
              </el-button>
              <el-button @click="newComment = ''">
                Clear
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
    </div>
  </div>
</template>
  
<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, User, ChatDotRound } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'TicketComments',
  components: {
    Refresh, User, ChatDotRound
  },
  props: {
    ticketId: {
      type: [String, Number],
      required: true
    }
  },
  setup(props) {
    const ticket = ref(null)
    const comments = ref([])
    const newComment = ref('')
    const loadingComments = ref(false)
    const submittingComment = ref(false)

    const loadTicket = async () => {
      try {
        const response = await axios.get(`/api/tickets/${props.ticketId}`)
        if (response.data) {
          ticket.value = response.data
        }
      } catch (error) {
        console.error('Error loading ticket:', error)
        ElMessage.error('Failed to load ticket details')
      }
    }

    const loadComments = async () => {
      try {
        loadingComments.value = true
        const response = await axios.get(`/api/comments/ticket/${props.ticketId}`)
        
        if (response.data.success) {
          comments.value = response.data.comments
        } else {
          ElMessage.error(response.data.message || 'Failed to load comments')
        }
      } catch (error) {
        console.error('Error loading comments:', error)
        ElMessage.error('Failed to load comments')
      } finally {
        loadingComments.value = false
      }
    }

    const addComment = async () => {
      if (!newComment.value.trim()) {
        ElMessage.warning('Please enter a comment')
        return
      }

      try {
        submittingComment.value = true
        
        const commentData = {
          comment: newComment.value.trim(),
          supportTicket: {
            id: props.ticketId
          }
        }

        const response = await axios.post('/api/comments', commentData)
        
        if (response.data.success) {
          comments.value.push(response.data.comment)
          newComment.value = ''
          ElMessage.success('Comment added successfully')
        } else {
          ElMessage.error(response.data.message || 'Failed to add comment')
        }
      } catch (error) {
        console.error('Error adding comment:', error)
        ElMessage.error('Failed to add comment')
      } finally {
        submittingComment.value = false
      }
    }

    const getStatusType = (status) => {
      const statusTypes = {
        'OPEN': 'primary',
        'IN_PROGRESS': 'warning',
        'RESOLVED': 'success',
        'CLOSED': 'info',
        'CANCELLED': 'danger'
      }
      return statusTypes[status] || 'info'
    }

    const getPriorityType = (priority) => {
      const priorityTypes = {
        'HIGH': 'danger',
        'MEDIUM': 'warning', 
        'LOW': 'success'
      }
      return priorityTypes[priority] || 'info'
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      const date = new Date(dateString)
      return date.toLocaleString()
    }

    onMounted(() => {
      loadTicket()
      loadComments()
    })

    return {
      ticket,
      comments,
      newComment,
      loadingComments,
      submittingComment,
      loadComments,
      addComment,
      getStatusType,
      getPriorityType,
      formatDate
    }
  }
}
</script>
  
<style scoped>
.ticket-comments-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.ticket-info {
  margin-bottom: 20px;
}

.ticket-card {
  border-radius: 8px;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ticket-header h3 {
  margin: 0;
  color: #303133;
}

.ticket-details {
  color: #606266;
}

.ticket-details p {
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ticket-description {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.ticket-description p {
  margin-top: 8px;
  line-height: 1.6;
  color: #606266;
}

.comments-section {
  margin-top: 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comments-header h3 {
  margin: 0;
  color: #303133;
}

.comments-list {
  min-height: 200px;
}

.no-comments {
  text-align: center;
  padding: 40px 20px;
}

.comment-item {
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
}

.comment-header {
  margin-bottom: 10px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-info strong {
  color: #303133;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  margin-left: 42px;
}

.comment-content p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.add-comment-section {
  margin-top: 20px;
  padding-top: 20px;
}

.add-comment-section .el-form-item {
  margin-bottom: 15px;
}

.add-comment-section .el-form-item:last-child {
  margin-bottom: 0;
}
</style>
