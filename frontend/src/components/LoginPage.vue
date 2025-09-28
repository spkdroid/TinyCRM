<template>
  <div class="login-container">
    <div class="background-animation"></div>
    
    <div class="login-card">
      <div class="login-header">
        <div class="logo-container">
          <div class="app-icon">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="48" height="48" rx="4" fill="#0078d4"/>
              <path d="M12 16h24v2H12v-2zm0 6h20v2H12v-2zm0 6h16v2H12v-2z" fill="white"/>
              <circle cx="36" cy="32" r="4" fill="white"/>
            </svg>
          </div>
        </div>
        <h1>TinyCRM</h1>
        <p class="subtitle">Customer Relationship Management</p>
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
                class="tinycrm-btn tinycrm-btn-primary tinycrm-btn-large"
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
              <div v-if="signupForm.password" class="password-strength">
                <div class="strength-bar">
                  <div 
                    class="strength-fill" 
                    :style="`width: ${passwordStrength.strength * 20}%; background-color: ${passwordStrength.color}`"
                  ></div>
                </div>
                <span class="strength-text" :style="`color: ${passwordStrength.color}`">
                  {{ passwordStrength.text }}
                </span>
              </div>
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

            <el-form-item prop="phoneNumber" class="form-item-enhanced">
              <el-input
                v-model="signupForm.phoneNumber"
                placeholder="Phone Number (Optional)"
                prefix-icon="Phone"
                size="large"
                :disabled="signupLoading"
                class="enhanced-input"
              />
            </el-form-item>

            <el-form-item prop="company" class="form-item-enhanced">
              <el-input
                v-model="signupForm.company"
                placeholder="Company (Optional)"
                prefix-icon="Office"
                size="large"
                :disabled="signupLoading"
                class="enhanced-input"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="signupLoading"
                @click="handleSignup"
                class="tinycrm-btn tinycrm-btn-primary tinycrm-btn-large"
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
import { ref, reactive, computed, onMounted } from 'vue'
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
      confirmPassword: '',
      phoneNumber: '',
      company: ''
    })

    const passwordStrength = computed(() => {
      const password = signupForm.password
      if (!password) return { strength: 0, text: '', color: '' }
      
      let score = 0
      const checks = {
        length: password.length >= 6,
        lowercase: /[a-z]/.test(password),
        uppercase: /[A-Z]/.test(password),
        numbers: /\d/.test(password),
        special: /[!@#$%^&*(),.?":{}|<>]/.test(password)
      }
      
      score = Object.values(checks).filter(Boolean).length
      
      if (score <= 1) return { strength: 1, text: 'Weak', color: '#f56565' }
      if (score <= 2) return { strength: 2, text: 'Fair', color: '#ed8936' }
      if (score <= 3) return { strength: 3, text: 'Good', color: '#38a169' }
      if (score <= 4) return { strength: 4, text: 'Strong', color: '#2b6cb0' }
      return { strength: 5, text: 'Very Strong', color: '#553c9a' }
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
      ],
      phoneNumber: [
        { 
          validator: (rule, value, callback) => {
            if (value && value.trim()) {
              const phoneRegex = /^[+]?[1-9]?[\d\s-()]{8,15}$/
              if (!phoneRegex.test(value.trim())) {
                callback(new Error('Please enter a valid phone number'))
              } else {
                callback()
              }
            } else {
              callback()
            }
          }, 
          trigger: 'blur' 
        }
      ],
      company: [
        { 
          validator: (rule, value, callback) => {
            if (value && value.trim() && value.trim().length < 2) {
              callback(new Error('Company name must be at least 2 characters'))
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
          phoneNumber: signupForm.phoneNumber.trim(),
          company: signupForm.company.trim(),
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
      passwordStrength,
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
  background: #f5f5f5;
  padding: 20px;
  position: relative;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Windows Background Pattern */
.background-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background: 
    radial-gradient(circle at 25% 25%, rgba(0, 120, 212, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(0, 120, 212, 0.03) 0%, transparent 50%);
  z-index: 1;
}

/* Remove floating shapes - keep clean Windows design */

.login-card {
  background: #ffffff;
  border: 1px solid #d1d1d1;
  border-radius: 8px;
  box-shadow: 0 4.8px 14.4px rgba(0, 0, 0, 0.18), 0 25.6px 57.6px rgba(0, 0, 0, 0.22);
  padding: 32px;
  width: 100%;
  max-width: 400px;
  position: relative;
  z-index: 2;
  animation: slideUp 0.3s ease-out;
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
  margin-bottom: 32px;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.app-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-header h1 {
  color: #323130;
  font-size: 28px;
  font-weight: 600;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.subtitle {
  color: #605e5c;
  font-size: 14px;
  font-weight: 400;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0;
}

/* Windows Tabs */
.auth-tabs {
  margin-bottom: 24px;
}

.auth-tabs :deep(.el-tabs__header) {
  margin: 0 0 24px 0;
}

.auth-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #d1d1d1;
}

.auth-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 400;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  color: #605e5c;
  transition: all 0.1s ease-in-out;
}

.auth-tabs :deep(.el-tabs__item.is-active) {
  color: #0078d4;
  font-weight: 600;
}

.auth-tabs :deep(.el-tabs__active-bar) {
  height: 2px;
  background-color: #0078d4;
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

/* Windows Fluent Design Button System */
.tinycrm-btn {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-weight: 400;
  font-size: 14px;
  border: 1px solid transparent;
  border-radius: 4px;
  padding: 8px 16px;
  min-width: 80px;
  height: 32px;
  background: transparent;
  color: #323130;
  cursor: pointer;
  transition: all 0.1s ease-in-out;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  outline: none;
  position: relative;
  user-select: none;
}

/* Button Sizes */
.tinycrm-btn-small {
  height: 24px;
  padding: 4px 12px;
  font-size: 12px;
  min-width: 64px;
}

.tinycrm-btn-medium {
  height: 32px;
  padding: 8px 16px;
  font-size: 14px;
  min-width: 80px;
}

.tinycrm-btn-large {
  height: 40px;
  padding: 10px 20px;
  font-size: 14px;
  min-width: 120px;
  width: 100%;
}

/* Windows Primary Button */
.tinycrm-btn-primary {
  background-color: #0078d4;
  border-color: #0078d4;
  color: #ffffff;
}

.tinycrm-btn-primary:hover {
  background-color: #106ebe;
  border-color: #106ebe;
}

.tinycrm-btn-primary:active {
  background-color: #005a9e;
  border-color: #005a9e;
}

.tinycrm-btn-primary:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px #0078d4;
}

/* Windows Success Button */
.tinycrm-btn-success {
  background-color: #107c10;
  border-color: #107c10;
  color: #ffffff;
}

.tinycrm-btn-success:hover {
  background-color: #0e700e;
  border-color: #0e700e;
}

.tinycrm-btn-success:active {
  background-color: #0c630c;
  border-color: #0c630c;
}

.tinycrm-btn-success:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px #107c10;
}

/* Windows Info Button */
.tinycrm-btn-info {
  background-color: #8764b8;
  border-color: #8764b8;
  color: #ffffff;
}

.tinycrm-btn-info:hover {
  background-color: #7c5fa6;
  border-color: #7c5fa6;
}

.tinycrm-btn-info:active {
  background-color: #715995;
  border-color: #715995;
}

.tinycrm-btn-info:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px #8764b8;
}

/* Windows Default Button */
.tinycrm-btn-default {
  background-color: #f3f2f1;
  border-color: #8a8886;
  color: #323130;
}

.tinycrm-btn-default:hover {
  background-color: #edebe9;
  border-color: #8a8886;
  color: #201f1e;
}

.tinycrm-btn-default:active {
  background-color: #e1dfdd;
  border-color: #8a8886;
  color: #201f1e;
}

.tinycrm-btn-default:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px #605e5c;
}

/* Windows Secondary Button (Outline) */
.tinycrm-btn-secondary {
  background-color: transparent;
  border-color: #8a8886;
  color: #323130;
}

.tinycrm-btn-secondary:hover {
  background-color: #f3f2f1;
  border-color: #323130;
  color: #323130;
}

.tinycrm-btn-secondary:active {
  background-color: #edebe9;
  border-color: #323130;
  color: #323130;
}

.tinycrm-btn-secondary:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px #605e5c;
}

/* Disabled State */
.tinycrm-btn:disabled {
  background-color: #f3f2f1;
  border-color: #c8c6c4;
  color: #a19f9d;
  cursor: not-allowed;
}

/* Loading State */
.tinycrm-btn.is-loading {
  pointer-events: none;
  opacity: 0.6;
}

/* Password Strength Indicator */
.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background-color: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-text {
  font-size: 12px;
  font-weight: 600;
  min-width: 80px;
  text-align: right;
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
