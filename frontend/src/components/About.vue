<template>
  <div class="about-container">
    <!-- Header Section -->
    <div class="about-header">
      <div class="logo-section">
        <div class="about-app-icon">
          <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="64" height="64" rx="6" fill="#0078d4"/>
            <path d="M16 22h32v3H16v-3zm0 8h28v3H16v-3zm0 8h24v3H16v-3z" fill="white"/>
            <circle cx="48" cy="42" r="6" fill="white"/>
          </svg>
        </div>
        <h1 class="app-title">TinyCRM</h1>
        <p class="app-subtitle">Enterprise Customer Relationship Management</p>
      </div>
    </div>

    <!-- Navigation Back -->
    <div class="nav-back">
      <el-button type="primary" plain @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        Back to Dashboard
      </el-button>
    </div>

    <!-- Main Content -->
    <div class="about-content">
      <el-row :gutter="24">
        <!-- About TinyCRM -->
        <el-col :xs="24" :md="12">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><InfoFilled /></el-icon>
                <span>About TinyCRM</span>
              </div>
            </template>
            <div class="card-content">
              <p class="description">
                TinyCRM is a modern, containerized Customer Relationship Management system designed for enterprises 
                seeking efficient customer interaction management, support ticketing, and collaborative comment tracking.
              </p>
              <p class="description">
                Built with cutting-edge technologies and enterprise-grade security, TinyCRM provides a seamless 
                experience for managing customer relationships, tracking support requests, and facilitating team collaboration.
              </p>
              
              <div class="features-list">
                <h4>Key Features:</h4>
                <ul>
                  <li><el-icon><TicketIcon /></el-icon> Advanced Support Ticket Management</li>
                  <li><el-icon><ChatRound /></el-icon> Collaborative Comment System</li>
                  <li><el-icon><User /></el-icon> Comprehensive User Management</li>
                  <li><el-icon><DataAnalysis /></el-icon> Real-time Dashboard Analytics</li>
                  <li><el-icon><Lock /></el-icon> Secure Authentication & Authorization</li>
                  <li><el-icon><Monitor /></el-icon> Responsive Cross-platform Design</li>
                </ul>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- Technology Stack -->
        <el-col :xs="24" :md="12">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Tools /></el-icon>
                <span>Technology Stack</span>
              </div>
            </template>
            <div class="card-content">
              <div class="tech-section">
                <h4><el-icon><Monitor /></el-icon> Frontend</h4>
                <div class="tech-badges">
                  <el-tag type="success" size="large">Modern JavaScript</el-tag>
                  <el-tag type="primary" size="large">Component Framework</el-tag>
                  <el-tag type="info" size="large">SPA Routing</el-tag>
                  <el-tag type="warning" size="large">HTTP Client</el-tag>
                </div>
              </div>

              <div class="tech-section">
                <h4><el-icon><Server /></el-icon> Backend</h4>
                <div class="tech-badges">
                  <el-tag type="success" size="large">Spring Boot 2.7.2</el-tag>
                  <el-tag type="primary" size="large">Spring Data JPA</el-tag>
                  <el-tag type="info" size="large">Hibernat</el-tag>
                  <el-tag type="warning" size="large">Gradle</el-tag>
                </div>
              </div>

              <div class="tech-section">
                <h4><el-icon><Coin /></el-icon> Database</h4>
                <div class="tech-badges">
                  <el-tag type="danger" size="large">MySQL 8.0</el-tag>
                  <el-tag type="info" size="large">Persistent Volumes</el-tag>
                </div>
              </div>

              <div class="tech-section">
                <h4><el-icon><Box /></el-icon> Deployment</h4>
                <div class="tech-badges">
                  <el-tag type="primary" size="large">Docker</el-tag>
                  <el-tag type="success" size="large">Docker Compose</el-tag>
                  <el-tag type="warning" size="large">Multi-environment</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Architecture Overview -->
      <el-row :gutter="24" class="architecture-row">
        <el-col :span="24">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Connection /></el-icon>
                <span>System Architecture</span>
              </div>
            </template>
            <div class="card-content">
              <div class="architecture-diagram">
                <div class="service-container">
                  <div class="service frontend-service">
                    <el-icon class="service-icon"><Monitor /></el-icon>
                    <h4>Frontend</h4>
                    <p>Modern Web Application</p>
                    <small>Port: 3000</small>
                  </div>
                  <div class="connection">↔</div>
                  <div class="service backend-service">
                    <el-icon class="service-icon"><Server /></el-icon>
                    <h4>Backend</h4>
                    <p>Spring Boot API</p>
                    <small>Port: 8080</small>
                  </div>
                  <div class="connection">↔</div>
                  <div class="service database-service">
                    <el-icon class="service-icon"><Coin /></el-icon>
                    <h4>Database</h4>
                    <p>MySQL Server</p>
                    <small>Port: 3306</small>
                  </div>
                </div>
                <div class="architecture-features">
                  <div class="feature-item">
                    <el-icon><CircleCheck /></el-icon>
                    <span>Containerized Services</span>
                  </div>
                  <div class="feature-item">
                    <el-icon><CircleCheck /></el-icon>
                    <span>Health Monitoring</span>
                  </div>
                  <div class="feature-item">
                    <el-icon><CircleCheck /></el-icon>
                    <span>Auto-scaling Ready</span>
                  </div>
                  <div class="feature-item">
                    <el-icon><CircleCheck /></el-icon>
                    <span>Development & Production Modes</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Version Information -->
      <el-row :gutter="24" class="version-row">
        <el-col :xs="24" :md="12">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Flag /></el-icon>
                <span>Version Information</span>
              </div>
            </template>
            <div class="card-content">
              <div class="version-info">
                <div class="version-item">
                  <strong>Current Version:</strong>
                  <el-tag type="success" size="large">v1.0.0</el-tag>
                </div>
                <div class="version-item">
                  <strong>Release Date:</strong>
                  <span>September 2025</span>
                </div>
                <div class="version-item">
                  <strong>Build:</strong>
                  <span>Production Ready</span>
                </div>
                <div class="version-item">
                  <strong>License:</strong>
                  <el-tag type="info">Apache 2.0</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- Author Information -->
        <el-col :xs="24" :md="12">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><User /></el-icon>
                <span>Developer</span>
              </div>
            </template>
            <div class="card-content">
              <div class="author-info">
                <div class="author-details">
                  <h4>Ramkumar Velmurugan</h4>
                  <p class="author-title">Full Stack Developer</p>
                  <div class="author-links">
                    <el-button type="primary" plain size="small" @click="openLink('http://www.spkdroid.com/CV/')">
                      <el-icon><Link /></el-icon>
                      Portfolio
                    </el-button>
                    <el-button type="info" plain size="small" @click="openLink('https://github.com/spkdroid')">
                      <el-icon><Star /></el-icon>
                      GitHub
                    </el-button>
                  </div>
                  <p class="author-description">
                    Passionate about building scalable, user-friendly applications that solve real-world business problems.
                  </p>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Footer -->
      <div class="about-footer">
        <el-divider>
          <el-icon><Star /></el-icon>
        </el-divider>
        <p class="footer-text">
          Thank you for using TinyCRM! If you find this tool helpful, please consider starring the repository.
        </p>
        <div class="footer-buttons">
          <el-button type="success" @click="openLink('https://github.com/spkdroid/TinyCRM')">
            <el-icon><Star /></el-icon>
            Star on GitHub
          </el-button>
          <el-button type="primary" @click="openLink('https://github.com/spkdroid/TinyCRM/issues')">
            <el-icon><QuestionFilled /></el-icon>
            Report Issues
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { 
  ArrowLeft, InfoFilled, Tools, Monitor, Server, Coin, Box, Connection, 
  CircleCheck, Flag, User, Link, Star, QuestionFilled, ChatRound, 
  DataAnalysis, Lock, TicketIcon 
} from '@element-plus/icons-vue'

export default {
  name: 'About',
  components: {
    ArrowLeft, InfoFilled, Tools, Monitor, Server, Coin, Box, Connection,
    CircleCheck, Flag, User, Link, Star, QuestionFilled, ChatRound,
    DataAnalysis, Lock, TicketIcon
  },
  setup() {
    const router = useRouter()

    const goBack = () => {
      router.push('/dashboard')
    }

    const openLink = (url) => {
      window.open(url, '_blank')
    }

    return {
      goBack,
      openLink
    }
  }
}
</script>

<style scoped>
.about-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.about-header {
  text-align: center;
  margin-bottom: 2rem;
  color: white;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.about-logo {
  width: 120px;
  height: 120px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  background: white;
  padding: 1rem;
}

.app-title {
  font-size: 3rem;
  font-weight: bold;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.app-subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0;
}

.nav-back {
  margin-bottom: 2rem;
}

.back-btn {
  border-color: white;
  color: white;
}

.back-btn:hover {
  background: white;
  color: #667eea;
}

.about-content {
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  margin-bottom: 2rem;
  border-radius: 12px;
  overflow: hidden;
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

.card-content {
  line-height: 1.6;
}

.description {
  margin-bottom: 1rem;
  color: #666;
}

.features-list h4 {
  color: #333;
  margin: 1rem 0 0.5rem 0;
}

.features-list ul {
  list-style: none;
  padding: 0;
}

.features-list li {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: #555;
}

.tech-section {
  margin-bottom: 1.5rem;
}

.tech-section h4 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.tech-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.architecture-row {
  margin-top: 2rem;
}

.architecture-diagram {
  text-align: center;
}

.service-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.service {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  min-width: 150px;
  transition: all 0.3s ease;
}

.service:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.frontend-service {
  border-color: #4FC08D;
}

.backend-service {
  border-color: #6DB33F;
}

.database-service {
  border-color: #4479A1;
}

.service-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: #667eea;
}

.service h4 {
  margin: 0.5rem 0;
  color: #333;
}

.service p {
  margin: 0.25rem 0;
  color: #666;
}

.service small {
  color: #999;
}

.connection {
  font-size: 1.5rem;
  color: #667eea;
  font-weight: bold;
}

.architecture-features {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 2rem;
  margin-top: 2rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #555;
}

.version-row {
  margin-top: 2rem;
}

.version-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.version-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.author-info {
  text-align: center;
}

.author-details h4 {
  color: #333;
  margin-bottom: 0.5rem;
}

.author-title {
  color: #666;
  font-style: italic;
  margin-bottom: 1rem;
}

.author-links {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.author-description {
  color: #555;
  font-size: 0.9rem;
  line-height: 1.5;
}

.about-footer {
  text-align: center;
  margin-top: 3rem;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: white;
}

.footer-text {
  margin: 1rem 0;
  font-size: 1.1rem;
}

.footer-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

/* Responsive Design */
@media (max-width: 768px) {
  .about-container {
    padding: 1rem;
  }
  
  .app-title {
    font-size: 2rem;
  }
  
  .service-container {
    flex-direction: column;
  }
  
  .connection {
    transform: rotate(90deg);
  }
  
  .architecture-features {
    flex-direction: column;
    align-items: center;
  }
  
  .author-links {
    flex-direction: column;
  }
  
  .footer-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>