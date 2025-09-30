<template>
  <div class="file-ticket">
    <!-- Page Header -->
    <div class="page-header">
      <h2>Create Support Ticket</h2>
      <p>Submit a new support request to our technical team</p>
    </div>

    <!-- Ticket Form -->
    <el-card class="form-card">
      <el-form
        ref="ticketForm"
        :model="ticketForm"
        :rules="formRules"
        label-width="120px"
        size="large"
        @submit.prevent="submitTicket"
      >
        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-form-item label="Subject" prop="subject">
              <el-input
                v-model="ticketForm.subject"
                placeholder="Brief description of the issue"
                :prefix-icon="EditPen"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="Category" prop="category">
              <el-select
                v-model="ticketForm.category"
                placeholder="Select category"
                style="width: 100%"
                :prefix-icon="FolderOpened"
              >
                <el-option label="Technical Support" value="Technical Support" />
                <el-option label="Bug Report" value="Bug Report" />
                <el-option label="Feature Request" value="Feature Request" />
                <el-option label="Account Issues" value="Account Issues" />
                <el-option label="Billing" value="Billing" />
                <el-option label="General Inquiry" value="General Inquiry" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-form-item label="Priority" prop="priority">
              <el-select
                v-model="ticketForm.priority"
                placeholder="Select priority"
                style="width: 100%"
                :prefix-icon="Flag"
              >
                <el-option label="Low" value="Low">
                  <div class="priority-option">
                    <el-tag type="success" size="small">Low</el-tag>
                    <span class="priority-desc">Non-urgent</span>
                  </div>
                </el-option>
                <el-option label="Medium" value="Medium">
                  <div class="priority-option">
                    <el-tag type="warning" size="small">Medium</el-tag>
                    <span class="priority-desc">Standard priority</span>
                  </div>
                </el-option>
                <el-option label="High" value="High">
                  <div class="priority-option">
                    <el-tag type="danger" size="small">High</el-tag>
                    <span class="priority-desc">Urgent</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="Assignee" prop="assignedTo">
              <el-select
                v-model="ticketForm.assignedTo"
                placeholder="Auto-assign or select user"
                style="width: 100%"
                :prefix-icon="User"
                clearable
                filterable
                :loading="loadingUsers"
              >
                <el-option label="Auto-assign" value="" />
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
                    <span class="user-role">{{ user.role }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Description" prop="description">
          <el-input
            v-model="ticketForm.description"
            type="textarea"
            :rows="6"
            placeholder="Provide detailed information about your issue. Include steps to reproduce, expected behavior, and any error messages."
            show-word-limit
            maxlength="2000"
          />
        </el-form-item>

        <el-form-item label="Attachments">
          <el-upload
            class="upload-demo"
            drag
            :action="uploadUrl"
            multiple
            :show-file-list="true"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              Drop files here or <em>click to upload</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                jpg/png/pdf files with a size less than 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="Tags">
          <el-tag
            v-for="tag in ticketForm.tags"
            :key="tag"
            class="tag-item"
            closable
            @close="removeTag(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            size="small"
            style="width: 120px"
            @keyup.enter="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button
            v-else
            class="button-new-tag"
            size="small"
            @click="showInput"
          >
            + Add Tag
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              @click="submitTicket"
              :icon="Check"
            >
              {{ submitting ? 'Creating Ticket...' : 'Create Ticket' }}
            </el-button>
            <el-button
              size="large"
              @click="resetForm"
              :icon="RefreshLeft"
            >
              Reset Form
            </el-button>
            <el-button
              size="large"
              @click="saveDraft"
              :icon="DocumentCopy"
            >
              Save as Draft
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ElMessage, ElNotification } from 'element-plus'
import {
  EditPen,
  FolderOpened,
  Flag,
  User,
  UploadFilled,
  Check,
  RefreshLeft,
  DocumentCopy
} from '@element-plus/icons-vue'

export default {
  name: 'FileTicket',
  components: {
    UploadFilled
  },
  data() {
    return {
      EditPen,
      FolderOpened,
      Flag,
      User,
      UploadFilled,
      Check,
      RefreshLeft,
      DocumentCopy,
      submitting: false,
      inputVisible: false,
      inputValue: '',
      loadingUsers: false,
      availableUsers: [],
      uploadUrl: `${this.$http.defaults.baseURL}/api/upload`,
      ticketForm: {
        subject: '',
        description: '',
        priority: 'Medium',
        category: '',
        assignedTo: '',
        tags: [],
        attachments: []
      },
      formRules: {
        subject: [
          { required: true, message: 'Please enter a subject', trigger: 'blur' },
          { min: 5, max: 200, message: 'Subject should be 5-200 characters', trigger: 'blur' }
        ],
        description: [
          { required: true, message: 'Please provide a description', trigger: 'blur' },
          { min: 10, message: 'Description should be at least 10 characters', trigger: 'blur' }
        ],
        priority: [
          { required: true, message: 'Please select a priority', trigger: 'change' }
        ],
        category: [
          { required: true, message: 'Please select a category', trigger: 'change' }
        ]
      }
    }
  },
  async mounted() {
    await this.loadAvailableUsers()
    
    // Load draft if exists
    const draft = localStorage.getItem('ticketDraft')
    if (draft) {
      const draftData = JSON.parse(draft)
      this.ticketForm = { ...this.ticketForm, ...draftData }
    }
  },
  methods: {
    async loadAvailableUsers() {
      try {
        this.loadingUsers = true
        const response = await this.$http.get('/api/tickets/assignable-users')
        
        if (response.data.success) {
          this.availableUsers = response.data.users
        } else {
          ElMessage.error('Failed to load assignable users')
        }
      } catch (error) {
        console.error('Error loading users:', error)
        ElMessage.error('Failed to load assignable users')
      } finally {
        this.loadingUsers = false
      }
    },
    async submitTicket() {
      try {
        const valid = await this.$refs.ticketForm.validate()
        if (!valid) return

        this.submitting = true

        const ticketData = {
          subject: this.ticketForm.subject,
          description: this.ticketForm.description,
          priority: this.ticketForm.priority,
          category: this.ticketForm.category,
          assigneeId: this.ticketForm.assignedTo || null,
          tags: this.ticketForm.tags,
          status: 'Open'
        }

        const response = await this.$http.post('/api/tickets', ticketData)

        if (response.status === 200 || response.status === 201) {
          ElNotification({
            title: 'Success',
            message: 'Support ticket created successfully!',
            type: 'success',
            duration: 3000
          })
          
          this.resetForm()
          this.$router.push('/tickets')
        }
      } catch (error) {
        console.error('Error creating ticket:', error)
        ElMessage.error('Failed to create ticket. Please try again.')
      } finally {
        this.submitting = false
      }
    },
    resetForm() {
      this.$refs.ticketForm.resetFields()
      this.ticketForm.tags = []
      this.ticketForm.attachments = []
    },
    saveDraft() {
      localStorage.setItem('ticketDraft', JSON.stringify(this.ticketForm))
      ElMessage.success('Draft saved successfully')
    },
    removeTag(tag) {
      this.ticketForm.tags.splice(this.ticketForm.tags.indexOf(tag), 1)
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(() => {
        this.$refs.InputRef.input.focus()
      })
    },
    handleInputConfirm() {
      if (this.inputValue && !this.ticketForm.tags.includes(this.inputValue)) {
        this.ticketForm.tags.push(this.inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    beforeUpload(file) {
      const isValidType = ['image/jpeg', 'image/png', 'application/pdf'].includes(file.type)
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isValidType) {
        ElMessage.error('Upload file must be JPG, PNG or PDF format!')
        return false
      }
      if (!isLt10M) {
        ElMessage.error('Upload file size cannot exceed 10MB!')
        return false
      }
      return true
    },
    handleUploadSuccess(response, file) {
      this.ticketForm.attachments.push({
        name: file.name,
        url: response.url
      })
      ElMessage.success('File uploaded successfully')
    },
    handleUploadError() {
      ElMessage.error('File upload failed')
    }
  }
}
</script>

<style scoped>
.file-ticket {
  max-width: 1000px;
  margin: 0 auto;
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

.form-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.priority-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.priority-desc {
  color: #909399;
  font-size: 12px;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.button-new-tag {
  margin-left: 8px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-start;
}

.upload-demo {
  margin-bottom: 12px;
}

:deep(.el-upload-dragger) {
  border-radius: 8px;
  border: 2px dashed #d9d9d9;
  transition: all 0.3s ease;
}

:deep(.el-upload-dragger:hover) {
  border-color: #409eff;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
}

.user-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-weight: 500;
  flex: 1;
}

.user-role {
  font-size: 12px;
  color: #909399;
  text-transform: uppercase;
}

:deep(.el-input__inner) {
  border-radius: 6px;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
}

:deep(.el-select .el-input__inner) {
  border-radius: 6px;
}

@media (max-width: 768px) {
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>
