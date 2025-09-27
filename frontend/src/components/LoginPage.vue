<template>
  <div class="login-container">
    <div class="background-animation">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>
    
    <div class="login-card">
      <div class="login-header">
        <div class="logo-container">
          <img src="/logo.png" alt="TinyCRM" class="logo" />
          <div class="logo-glow"></div>
        </div>
        <h1>Welcome to TinyCRM</h1>
        <p class="subtitle">Enterprise Customer Relationship Management</p>
      </div>

      <el-tabs v-model="activeTab" class="auth-tabs" @tab-change="resetForms">
        <el-tab-pane label="Sign In" name="login">
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="auth-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username" class="form-item-enhanced">
              <el-input
                v-model="loginForm.username"
                placeholder="Username or Email"
                prefix-icon="User"
                size="large"
                :disabled="loading"
                class="enhanced-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item-enhanced">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="Password"
                prefix-icon="Lock"
                size="large"
                :disabled="loading"
                show-password
                class="enhanced-input"
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item class="form-options">
              <div class="options-row">
                <el-checkbox v-model="loginForm.rememberMe" :disabled="loading" class="remember-me">
                  Remember me
                </el-checkbox>
                <el-link type="primary" @click="showForgotPassword" class="forgot-link">
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
                class="auth-button login-button"
              >
                <el-icon v-if="!loading"><UserFilled /></el-icon>
                {{ loading ? 'Signing in...' : 'Sign In' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="Create Account" name="register">
          <el-form
            ref="signupFormRef"
            :model="signupForm"
            :rules="signupRules"
            class="auth-form"
            @submit.prevent="handleSignup"
          >
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item prop="firstName" class="form-item-enhanced">
                  <el-input
                    v-model="signupForm.firstName"
                    placeholder="First Name"
                    prefix-icon="User"
                    size="large"
                    :disabled="signupLoading"
                    class="enhanced-input"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="lastName" class="form-item-enhanced">
                  <el-input
                    v-model="signupForm.lastName"
                    placeholder="Last Name"
                    prefix-icon="User"
                    size="large"
                    :disabled="signupLoading"
                    class="enhanced-input"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item prop="username" class="form-item-enhanced">
              <el-input
                v-model="signupForm.username"
                placeholder="Username"
                prefix-icon="UserFilled"
                size="large"
                :disabled="signupLoading"
                class="enhanced-input"
              />
            </el-form-item>

            <el-form-item prop="email" class="form-item-enhanced">
              <el-input
                v-model="signupForm.email"
                type="email"
                placeholder="Email Address"
                prefix-icon="Message"
                size="large"
                :disabled="signupLoading"
                class="enhanced-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item-enhanced">
              <el-input
                v-model="signupForm.password"
                type="password"
                placeholder="Password (min 6 characters)"
                prefix-icon="Lock"
                size="large"
                :disabled="signupLoading"
                show-password
                class="enhanced-input"
              />
            </el-form-item>

            <el-form-item prop="confirmPassword" class="form-item-enhanced">
              <el-input
                v-model="signupForm.confirmPassword"
                type="password"
                placeholder="Confirm Password"
                prefix-icon="Lock"
                size="large"
                :disabled="signupLoading"
                show-password
                class="enhanced-input"
                @keyup.enter="handleSignup"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="signupLoading"
                @click="handleSignup"
                class="auth-button signup-button"
              >
                <el-icon v-if="!signupLoading"><CirclePlus /></el-icon>
                {{ signupLoading ? 'Creating Account...' : 'Create Account' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

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


  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { User, Lock, UserFilled, Message, CirclePlus } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'LoginPage',
  components: {
    User,
    Lock,
    UserFilled,
    Message,
    CirclePlus
  },
  setup() {
    const router = useRouter()
    const activeTab = ref('login')
    
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

    const signupRules = {
      firstName: [
        { required: true, message: 'Please enter your first name', trigger: 'blur' },
        { min: 2, message: 'First name must be at least 2 characters', trigger: 'blur' }
      ],
      lastName: [
        { required: true, message: 'Please enter your last name', trigger: 'blur' },
        { min: 2, message: 'Last name must be at least 2 characters', trigger: 'blur' }
      ],
      username: [
        { required: true, message: 'Please enter a username', trigger: 'blur' },
        { min: 3, message: 'Username must be at least 3 characters', trigger: 'blur' },
        { pattern: /^[a-zA-Z0-9_]+$/, message: 'Username can only contain letters, numbers, and underscores', trigger: 'blur' }
      ],
      email: [
        { required: true, message: 'Please enter your email address', trigger: 'blur' },
        { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
      ],
      password: [
        { required: true, message: 'Please enter a password', trigger: 'blur' },
        { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' },
        { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: 'Password must contain at least one letter and one number', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: 'Please confirm your password', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== signupForm.password) {
              callback(new Error('Passwords do not match'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
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

    const resetForms = () => {
      // Reset forms when switching tabs
      Object.keys(loginForm).forEach(key => {
        if (key !== 'rememberMe') loginForm[key] = ''
      })
      Object.keys(signupForm).forEach(key => {
        signupForm[key] = ''
      })
    }

    const handleSignup = async () => {
      const signupFormRef = document.querySelector('.auth-form .el-form')
      if (!signupFormRef) {
        ElMessage.error('Form not found')
        return
      }

      try {
        signupLoading.value = true
        
        // Validate form fields
        const isValid = await new Promise((resolve) => {
          signupFormRef.validate((valid) => {
            resolve(valid)
          })
        })

        if (!isValid) {
          ElMessage.error('Please fix the form errors before submitting')
          return
        }
        
        const response = await axios.post('/api/auth/register', {
          username: signupForm.username.trim(),
          email: signupForm.email.trim().toLowerCase(),
          firstName: signupForm.firstName.trim(),
          lastName: signupForm.lastName.trim(),
          password: signupForm.password,
          role: 'USER'
        })

        if (response.data.success) {
          ElNotification({
            title: 'Account Created Successfully!',
            message: `Welcome ${signupForm.firstName}! Your account has been created. Please sign in to continue.`,
            type: 'success',
            duration: 5000
          })
          
          // Switch to login tab and pre-fill username
          activeTab.value = 'login'
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
        const errorMessage = error.response?.data?.message || 'Registration failed. Please try again.'
        ElMessage.error(errorMessage)
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
      activeTab,
      loginForm,
      signupForm,
      loading,
      signupLoading,
      showDemoAccounts,
      loginRules,
      signupRules,
      handleLogin,
      handleSignup,
      resetForms,
      loginAsDemo,
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
  position: relative;
  overflow: hidden;
}

/* Background Animation */
.background-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 20s infinite linear;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  left: 80%;
  animation-delay: -5s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 80%;
  left: 20%;
  animation-delay: -10s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 70%;
  animation-delay: -15s;
}

.shape-5 {
  width: 40px;
  height: 40px;
  top: 40%;
  left: 5%;
  animation-delay: -7s;
}

@keyframes float {
  0% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.3;
  }
  100% {
    transform: translateY(0px) rotate(360deg);
    opacity: 0.7;
  }
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  padding: 48px;
  width: 100%;
  max-width: 480px;
  position: relative;
  z-index: 2;
  animation: slideUp 0.8s ease-out;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  position: relative;
  display: inline-block;
  margin-bottom: 24px;
}

.logo {
  width: 80px;
  height: 80px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  transition: transform 0.3s ease;
}

.logo-glow {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.login-header h1 {
  color: #2c3e50;
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  opacity: 0.8;
}

/* Enhanced Tabs */
.auth-tabs {
  margin-bottom: 32px;
}

.auth-tabs :deep(.el-tabs__header) {
  margin: 0 0 32px 0;
}

.auth-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.auth-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 600;
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
  color: #64748b;
  transition: all 0.3s ease;
}

.auth-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
}

.auth-tabs :deep(.el-tabs__active-bar) {
  display: none;
}

/* Enhanced Form */
.auth-form {
  margin-bottom: 24px;
}

.form-item-enhanced {
  margin-bottom: 24px;
}

.enhanced-input :deep(.el-input__wrapper) {
  background: rgba(248, 250, 252, 0.8);
  border: 2px solid rgba(226, 232, 240, 0.8);
  border-radius: 16px;
  padding: 0 20px;
  height: 56px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.enhanced-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.9);
}

.enhanced-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.enhanced-input :deep(.el-input__inner) {
  font-size: 15px;
  font-weight: 500;
  color: #334155;
}

.enhanced-input :deep(.el-input__prefix) {
  color: #64748b;
}

/* Form Options */
.form-options {
  margin-bottom: 32px;
}

.options-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.remember-me :deep(.el-checkbox__label) {
  font-weight: 500;
  color: #64748b;
}

.forgot-link {
  font-weight: 600;
  text-decoration: none;
}

/* Enhanced Buttons */
.auth-button {
  width: 100%;
  height: 56px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 16px;
  border: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.4);
}

.signup-button {
  background: linear-gradient(135deg, #4ade80 0%, #22c55e 100%);
  box-shadow: 0 8px 20px rgba(34, 197, 94, 0.3);
}

.signup-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(34, 197, 94, 0.4);
}

.auth-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.auth-button:hover::before {
  left: 100%;
}

/* Demo Accounts */
.demo-accounts {
  margin-top: 32px;
  text-align: center;
}

.demo-accounts :deep(.el-divider__text) {
  color: #64748b;
  font-weight: 500;
}

.demo-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 16px;
}

.demo-buttons .el-button {
  border-radius: 12px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  background: rgba(102, 126, 234, 0.05);
  color: #667eea;
  font-weight: 500;
  transition: all 0.3s ease;
}

.demo-buttons .el-button:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

/* Responsive Design */
@media (max-width: 640px) {
  .login-card {
    padding: 32px 24px;
    margin: 16px;
    max-width: none;
    border-radius: 20px;
  }
  
  .login-header h1 {
    font-size: 28px;
  }
  
  .logo {
    width: 64px;
    height: 64px;
  }
  
  .demo-buttons {
    flex-direction: column;
  }
  
  .auth-tabs :deep(.el-tabs__item) {
    padding: 0 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 12px;
  }
  
  .login-card {
    padding: 24px 20px;
  }
  
  .enhanced-input :deep(.el-input__wrapper) {
    height: 48px;
    padding: 0 16px;
  }
  
  .auth-button {
    height: 48px;
  }
}

/* Loading States */
.auth-button.is-loading {
  pointer-events: none;
}

.auth-button.is-loading::before {
  display: none;
}

/* Validation Error States */
.form-item-enhanced :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #ef4444;
  background: rgba(239, 68, 68, 0.05);
}

.form-item-enhanced :deep(.el-form-item__error) {
  font-size: 12px;
  font-weight: 500;
  margin-top: 8px;
  color: #ef4444;
}

/* Success States */
.form-item-enhanced :deep(.el-form-item.is-success .el-input__wrapper) {
  border-color: #22c55e;
  background: rgba(34, 197, 94, 0.05);
}
</style>
