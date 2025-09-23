<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <img src="/logo.png" alt="TinyCRM" class="logo" />
        <h1>Welcome to TinyCRM</h1>
        <p class="subtitle">Please sign in to continue</p>
      </div>

      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="Username or Email"
            prefix-icon="User"
            size="large"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="Password"
            prefix-icon="Lock"
            size="large"
            :disabled="loading"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe" :disabled="loading">
              Remember me
            </el-checkbox>
            <el-link type="primary" @click="showForgotPassword">
              Forgot password?
            </el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            <el-icon v-if="!loading"><UserFilled /></el-icon>
            {{ loading ? 'Signing in...' : 'Sign In' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>Don't have an account?</p>
        <el-button type="text" @click="showSignup" :disabled="loading">
          Create Account
        </el-button>
      </div>

      <div class="demo-accounts" v-if="showDemoAccounts">
        <el-divider>Demo Accounts</el-divider>
        <div class="demo-buttons">
          <el-button size="small" @click="loginAsDemo('admin')" :disabled="loading">
            Admin (admin/admin123)
          </el-button>
          <el-button size="small" @click="loginAsDemo('support')" :disabled="loading">
            Support (support/support123)
          </el-button>
        </div>
      </div>
    </div>

    <!-- Signup Modal -->
    <el-dialog
      v-model="signupVisible"
      title="Create Account"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="signupForm"
        :model="signupForm"
        label-width="100px"
        class="signup-form"
      >
        <el-form-item label="Username" required>
          <el-input 
            v-model="signupForm.username" 
            :disabled="signupLoading"
            placeholder="Enter username"
            clearable
          />
        </el-form-item>

        <el-form-item label="Email" required>
          <el-input 
            v-model="signupForm.email" 
            type="email" 
            :disabled="signupLoading"
            placeholder="Enter email address"
            clearable
          />
        </el-form-item>

        <el-form-item label="First Name" required>
          <el-input 
            v-model="signupForm.firstName" 
            :disabled="signupLoading"
            placeholder="Enter first name"
            clearable
          />
        </el-form-item>

        <el-form-item label="Last Name" required>
          <el-input 
            v-model="signupForm.lastName" 
            :disabled="signupLoading"
            placeholder="Enter last name"
            clearable
          />
        </el-form-item>

        <el-form-item label="Password" required>
          <el-input
            v-model="signupForm.password"
            type="password"
            show-password
            :disabled="signupLoading"
            placeholder="Enter password (min 6 chars)"
            clearable
          />
        </el-form-item>

        <el-form-item label="Confirm Password" required>
          <el-input
            v-model="signupForm.confirmPassword"
            type="password"
            show-password
            :disabled="signupLoading"
            placeholder="Confirm password"
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="signupVisible = false" :disabled="signupLoading">
            Cancel
          </el-button>
          <el-button type="primary" @click="handleSignup" :loading="signupLoading">
            Create Account
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { User, Lock, UserFilled } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'LoginPage',
  components: {
    User,
    Lock,
    UserFilled
  },
  setup() {
    const router = useRouter()
    const loginForm = reactive({
      username: '',
      password: '',
      rememberMe: false
    })

    const signupForm = reactive({
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: ''
    })

    const loading = ref(false)
    const signupLoading = ref(false)
    const signupVisible = ref(false)
    const showDemoAccounts = ref(true)

    const loginRules = {
      username: [
        { required: true, message: 'Please enter username or email', trigger: 'blur' }
      ],
      password: [
        { required: true, message: 'Please enter password', trigger: 'blur' },
        { min: 3, message: 'Password must be at least 3 characters', trigger: 'blur' }
      ]
    }



    const handleLogin = async () => {
      try {
        loading.value = true
        
        const response = await axios.post('/api/auth/login', {
          username: loginForm.username,
          password: loginForm.password,
          rememberMe: loginForm.rememberMe
        })

        if (response.data.success) {
          // Store user data in localStorage for persistence
          localStorage.setItem('user', JSON.stringify(response.data.user))
          localStorage.setItem('sessionToken', response.data.sessionToken)
          
          ElNotification({
            title: 'Success',
            message: `Welcome back, ${response.data.user.firstName || response.data.user.username}!`,
            type: 'success'
          })

          // Redirect to dashboard
          router.push('/dashboard')
        } else {
          ElMessage.error(response.data.message || 'Login failed')
        }
      } catch (error) {
        console.error('Login error:', error)
        ElMessage.error(error.response?.data?.message || 'Login failed. Please try again.')
      } finally {
        loading.value = false
      }
    }

    const handleSignup = async () => {
      // Validate form first
      const signupFormRef = document.querySelector('.signup-form')
      if (!signupFormRef) {
        ElMessage.error('Form not found')
        return
      }

      // Check required fields manually
      if (!signupForm.username || !signupForm.email || !signupForm.firstName || 
          !signupForm.lastName || !signupForm.password || !signupForm.confirmPassword) {
        ElMessage.error('Please fill in all required fields')
        return
      }

      // Check password match
      if (signupForm.password !== signupForm.confirmPassword) {
        ElMessage.error('Passwords do not match')
        return
      }

      // Check password length
      if (signupForm.password.length < 6) {
        ElMessage.error('Password must be at least 6 characters long')
        return
      }

      try {
        signupLoading.value = true
        
        const response = await axios.post('/api/auth/register', {
          username: signupForm.username,
          email: signupForm.email,
          firstName: signupForm.firstName,
          lastName: signupForm.lastName,
          password: signupForm.password,
          role: 'USER'
        })

        if (response.data.success) {
          ElNotification({
            title: 'Success',
            message: 'Account created successfully! Please sign in.',
            type: 'success'
          })
          
          signupVisible.value = false
          
          // Pre-fill login form
          loginForm.username = signupForm.username
          
          // Reset signup form
          Object.keys(signupForm).forEach(key => {
            signupForm[key] = ''
          })
        } else {
          ElMessage.error(response.data.message || 'Registration failed')
        }
      } catch (error) {
        console.error('Signup error:', error)
        ElMessage.error(error.response?.data?.message || 'Registration failed. Please try again.')
      } finally {
        signupLoading.value = false
      }
    }

    const loginAsDemo = (type) => {
      if (type === 'admin') {
        loginForm.username = 'admin'
        loginForm.password = 'admin123'
      } else if (type === 'support') {
        loginForm.username = 'support'
        loginForm.password = 'support123'
      }
      handleLogin()
    }

    const showSignup = () => {
      signupVisible.value = true
    }

    const showForgotPassword = () => {
      ElMessage.info('Please contact your administrator to reset your password.')
    }

    // Check if user is already logged in
    onMounted(async () => {
      const sessionToken = localStorage.getItem('sessionToken')
      if (sessionToken) {
        try {
          const response = await axios.get('/api/auth/me')
          if (response.data.success) {
            router.push('/dashboard')
          } else {
            localStorage.removeItem('user')
            localStorage.removeItem('sessionToken')
          }
        } catch (error) {
          localStorage.removeItem('user')
          localStorage.removeItem('sessionToken')
        }
      }
    })

    return {
      loginForm,
      signupForm,
      loading,
      signupLoading,
      signupVisible,
      showDemoAccounts,
      loginRules,
      handleLogin,
      handleSignup,
      loginAsDemo,
      showSignup,
      showForgotPassword
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.login-header h1 {
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #7f8c8d;
  font-size: 16px;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.login-footer {
  text-align: center;
  color: #7f8c8d;
}

.login-footer p {
  margin: 0 0 8px 0;
}

.demo-accounts {
  margin-top: 20px;
  text-align: center;
}

.demo-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.dialog-footer {
  text-align: right;
}

/* Responsive design */
@media (max-width: 480px) {
  .login-card {
    padding: 24px;
    margin: 0;
  }
  
  .demo-buttons {
    flex-direction: column;
  }
}
</style>
