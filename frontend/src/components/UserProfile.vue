<template>
  <div class="profile-container">
    <!-- Header -->
    <div class="profile-header">
      <div class="header-content">
        <div class="user-info">
          <div class="avatar-section">
            <el-avatar 
              :size="100" 
              :src="profileData.avatarUrl" 
              class="user-avatar"
            >
              <el-icon><User /></el-icon>
            </el-avatar>
            <el-button 
              type="primary" 
              size="small" 
              @click="showAvatarUpload = true"
              class="upload-btn"
            >
              <el-icon><Camera /></el-icon>
              Change Photo
            </el-button>
          </div>
          <div class="user-details">
            <h2>{{ profileData.firstName }} {{ profileData.lastName }}</h2>
            <p class="username">@{{ profileData.username }}</p>
            <p class="job-title" v-if="profileData.jobTitle">{{ profileData.jobTitle }}</p>
            <p class="company" v-if="profileData.company">{{ profileData.company }}</p>
          </div>
        </div>
        <div class="header-actions">
          <el-button v-if="!isEditing" type="primary" @click="startEditing">
            <el-icon><Edit /></el-icon>
            Edit Profile
          </el-button>
          <div v-else class="edit-actions">
            <el-button type="success" @click="saveProfile" :loading="saving">
              <el-icon><Check /></el-icon>
              Save Changes
            </el-button>
            <el-button @click="cancelEditing">
              <el-icon><Close /></el-icon>
              Cancel
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Profile Form -->
    <div class="profile-content">
      <el-row :gutter="24">
        <!-- Personal Information -->
        <el-col :xs="24" :lg="12">
          <el-card class="profile-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><User /></el-icon>
                <span>Personal Information</span>
              </div>
            </template>
            
            <el-form :model="profileData" :rules="profileRules" ref="personalFormRef" label-width="120px">
              <el-form-item label="First Name" prop="firstName">
                <el-input 
                  v-model="profileData.firstName" 
                  :disabled="!isEditing"
                  placeholder="Enter your first name"
                />
              </el-form-item>
              
              <el-form-item label="Last Name" prop="lastName">
                <el-input 
                  v-model="profileData.lastName" 
                  :disabled="!isEditing"
                  placeholder="Enter your last name"
                />
              </el-form-item>
              
              <el-form-item label="Email" prop="email">
                <el-input 
                  v-model="profileData.email" 
                  :disabled="!isEditing"
                  type="email"
                  placeholder="Enter your email"
                />
              </el-form-item>
              
              <el-form-item label="Phone Number" prop="phoneNumber">
                <el-input 
                  v-model="profileData.phoneNumber" 
                  :disabled="!isEditing"
                  placeholder="Enter your phone number"
                />
              </el-form-item>
              
              <el-form-item label="Date of Birth" prop="dateOfBirth">
                <el-date-picker
                  v-model="profileData.dateOfBirth"
                  :disabled="!isEditing"
                  type="date"
                  placeholder="Select your date of birth"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="Bio" prop="bio">
                <el-input 
                  v-model="profileData.bio" 
                  :disabled="!isEditing"
                  type="textarea"
                  :rows="4"
                  placeholder="Tell us about yourself..."
                />
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- Address Information -->
        <el-col :xs="24" :lg="12">
          <el-card class="profile-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Location /></el-icon>
                <span>Address Information</span>
              </div>
            </template>
            
            <el-form :model="profileData" label-width="120px">
              <el-form-item label="Address">
                <el-input 
                  v-model="profileData.address" 
                  :disabled="!isEditing"
                  placeholder="Enter your address"
                />
              </el-form-item>
              
              <el-form-item label="City">
                <el-input 
                  v-model="profileData.city" 
                  :disabled="!isEditing"
                  placeholder="Enter your city"
                />
              </el-form-item>
              
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="State">
                    <el-input 
                      v-model="profileData.state" 
                      :disabled="!isEditing"
                      placeholder="State"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Zip Code">
                    <el-input 
                      v-model="profileData.zipCode" 
                      :disabled="!isEditing"
                      placeholder="Zip Code"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="Country">
                <el-select 
                  v-model="profileData.country" 
                  :disabled="!isEditing"
                  placeholder="Select your country"
                  style="width: 100%"
                  filterable
                >
                  <el-option v-for="country in countries" :key="country" :label="country" :value="country" />
                </el-select>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="24" class="second-row">
        <!-- Work Information -->
        <el-col :xs="24" :lg="12">
          <el-card class="profile-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Briefcase /></el-icon>
                <span>Work Information</span>
              </div>
            </template>
            
            <el-form :model="profileData" label-width="120px">
              <el-form-item label="Job Title">
                <el-input 
                  v-model="profileData.jobTitle" 
                  :disabled="!isEditing"
                  placeholder="Enter your job title"
                />
              </el-form-item>
              
              <el-form-item label="Department">
                <el-input 
                  v-model="profileData.department" 
                  :disabled="!isEditing"
                  placeholder="Enter your department"
                />
              </el-form-item>
              
              <el-form-item label="Company">
                <el-input 
                  v-model="profileData.company" 
                  :disabled="!isEditing"
                  placeholder="Enter your company"
                />
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- Preferences -->
        <el-col :xs="24" :lg="12">
          <el-card class="profile-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Setting /></el-icon>
                <span>Preferences</span>
              </div>
            </template>
            
            <el-form :model="profileData" label-width="120px">
              <el-form-item label="Timezone">
                <el-select 
                  v-model="profileData.timezone" 
                  :disabled="!isEditing"
                  placeholder="Select timezone"
                  style="width: 100%"
                  filterable
                >
                  <el-option v-for="tz in timezones" :key="tz.value" :label="tz.label" :value="tz.value" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="Language">
                <el-select 
                  v-model="profileData.language" 
                  :disabled="!isEditing"
                  placeholder="Select language"
                  style="width: 100%"
                >
                  <el-option label="English" value="en" />
                  <el-option label="Spanish" value="es" />
                  <el-option label="French" value="fr" />
                  <el-option label="German" value="de" />
                  <el-option label="Chinese" value="zh" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="Notifications">
                <div class="notification-settings">
                  <el-checkbox 
                    v-model="profileData.emailNotifications" 
                    :disabled="!isEditing"
                  >
                    Email Notifications
                  </el-checkbox>
                  <el-checkbox 
                    v-model="profileData.smsNotifications" 
                    :disabled="!isEditing"
                  >
                    SMS Notifications
                  </el-checkbox>
                </div>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Avatar Upload Dialog -->
    <el-dialog
      v-model="showAvatarUpload"
      title="Upload Avatar"
      width="400px"
      :before-close="handleAvatarDialogClose"
    >
      <el-upload
        ref="uploadRef"
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :before-upload="beforeAvatarUpload"
        :http-request="handleAvatarUpload"
      >
        <el-avatar v-if="profileData.avatarUrl" :src="profileData.avatarUrl" :size="150">
          <el-icon><User /></el-icon>
        </el-avatar>
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <div class="upload-tip">
        JPG/PNG files with a size less than 5MB
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAvatarUpload = false">Cancel</el-button>
          <el-button type="primary" @click="showAvatarUpload = false">Done</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'

import { ElMessage, ElNotification } from 'element-plus'
import { 
  User, Edit, Check, Close, Camera, Location, Briefcase, Setting, Plus 
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'UserProfile',
  components: {
    User, Edit, Check, Close, Camera, Location, Briefcase, Setting, Plus
  },
  setup() {
    const isEditing = ref(false)
    const saving = ref(false)
    const loading = ref(true)
    const showAvatarUpload = ref(false)
    
    const profileData = reactive({
      id: null,
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      phoneNumber: '',
      address: '',
      city: '',
      state: '',
      zipCode: '',
      country: '',
      bio: '',
      avatarUrl: '',
      jobTitle: '',
      department: '',
      company: '',
      dateOfBirth: null,
      timezone: 'UTC',
      language: 'en',
      emailNotifications: true,
      smsNotifications: false
    })

    const originalData = reactive({})
    
    const profileRules = {
      firstName: [
        { required: true, message: 'Please enter your first name', trigger: 'blur' }
      ],
      lastName: [
        { required: true, message: 'Please enter your last name', trigger: 'blur' }
      ],
      email: [
        { required: true, message: 'Please enter your email', trigger: 'blur' },
        { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
      ]
    }

    const countries = [
      'United States', 'Canada', 'United Kingdom', 'Germany', 'France', 'Spain', 
      'Italy', 'Australia', 'Japan', 'China', 'India', 'Brazil', 'Mexico', 'Other'
    ]

    const timezones = [
      { label: 'UTC', value: 'UTC' },
      { label: 'Eastern Time (ET)', value: 'America/New_York' },
      { label: 'Central Time (CT)', value: 'America/Chicago' },
      { label: 'Mountain Time (MT)', value: 'America/Denver' },
      { label: 'Pacific Time (PT)', value: 'America/Los_Angeles' },
      { label: 'London (GMT)', value: 'Europe/London' },
      { label: 'Paris (CET)', value: 'Europe/Paris' },
      { label: 'Tokyo (JST)', value: 'Asia/Tokyo' },
      { label: 'Sydney (AEST)', value: 'Australia/Sydney' }
    ]

    const loadProfile = async () => {
      try {
        loading.value = true
        const response = await axios.get('/api/profile')
        
        if (response.data.success) {
          Object.assign(profileData, response.data.profile)
          Object.assign(originalData, response.data.profile)
        } else {
          ElMessage.error(response.data.message || 'Failed to load profile')
        }
      } catch (error) {
        console.error('Profile load error:', error)
        ElMessage.error('Failed to load profile')
      } finally {
        loading.value = false
      }
    }

    const startEditing = () => {
      isEditing.value = true
      Object.assign(originalData, profileData)
    }

    const cancelEditing = () => {
      isEditing.value = false
      Object.assign(profileData, originalData)
    }

    const saveProfile = async () => {
      try {
        saving.value = true
        const response = await axios.put('/api/profile', profileData)
        
        if (response.data.success) {
          Object.assign(profileData, response.data.profile)
          Object.assign(originalData, response.data.profile)
          isEditing.value = false
          ElNotification({
            title: 'Success',
            message: 'Profile updated successfully!',
            type: 'success'
          })
        } else {
          ElMessage.error(response.data.message || 'Failed to update profile')
        }
      } catch (error) {
        console.error('Profile save error:', error)
        ElMessage.error('Failed to update profile')
      } finally {
        saving.value = false
      }
    }

    const beforeAvatarUpload = (file) => {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isJPG) {
        ElMessage.error('Avatar picture must be JPG or PNG format!')
      }
      if (!isLt5M) {
        ElMessage.error('Avatar picture size cannot exceed 5MB!')
      }
      return isJPG && isLt5M
    }

    const handleAvatarUpload = async (params) => {
      const formData = new FormData()
      formData.append('avatar', params.file)
      
      try {
        const response = await axios.post('/api/profile/avatar', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data.success) {
          profileData.avatarUrl = response.data.avatarUrl
          ElMessage.success('Avatar uploaded successfully!')
        } else {
          ElMessage.error(response.data.message || 'Failed to upload avatar')
        }
      } catch (error) {
        console.error('Avatar upload error:', error)
        ElMessage.error('Failed to upload avatar')
      }
    }

    const handleAvatarDialogClose = () => {
      showAvatarUpload.value = false
    }

    onMounted(() => {
      loadProfile()
    })

    return {
      profileData,
      isEditing,
      saving,
      loading,
      showAvatarUpload,
      profileRules,
      countries,
      timezones,
      startEditing,
      cancelEditing,
      saveProfile,
      beforeAvatarUpload,
      handleAvatarUpload,
      handleAvatarDialogClose
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: #f8f9fa;
  min-height: 100vh;
}

.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 2rem;
  margin-bottom: 2rem;
  color: white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 2rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.user-avatar {
  border: 4px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.upload-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.upload-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.user-details h2 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  font-weight: bold;
}

.username {
  margin: 0 0 0.5rem 0;
  opacity: 0.8;
  font-size: 1.1rem;
}

.job-title {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  font-weight: 500;
}

.company {
  margin: 0;
  opacity: 0.9;
  font-size: 0.9rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.edit-actions {
  display: flex;
  gap: 1rem;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.second-row {
  margin-top: 0;
}

.profile-card {
  height: 100%;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: bold;
  font-size: 1.1rem;
}

.header-icon {
  font-size: 1.2rem;
  color: #667eea;
}

.notification-settings {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .3s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 150px;
  height: 150px;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  text-align: center;
  line-height: 150px;
}

.upload-tip {
  margin-top: 1rem;
  color: #606266;
  font-size: 12px;
  text-align: center;
}

/* Responsive Design */
@media (max-width: 768px) {
  .profile-container {
    padding: 1rem;
  }
  
  .header-content {
    flex-direction: column;
    text-align: center;
  }
  
  .user-info {
    flex-direction: column;
    text-align: center;
  }
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
  
  .edit-actions {
    flex-direction: column;
    width: 100%;
  }
}
</style>