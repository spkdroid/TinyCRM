<h1 align="center">
  <br>
  <img src="./img/logo.png" alt="TinyCRM Logo" width="220">
  <br>
  TinyCRM
  <br>
</h1>

<p align="center">
  <i>A modern, containerized Customer Relationship Management (CRM) system with enterprise-grade features, built with Spring Boot, Vue.js, and MySQL.</i>
</p>

<div align="center">

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

</div>

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🚀 Quick Start](#-quick-start)
- [🔧 Development Setup](#-development-setup)
- [🔐 Authentication](#-authentication)
- [📚 API Endpoints](#-api-endpoints)
- [🐳 Docker Configuration](#-docker-configuration)
- [🎯 Usage Guide](#-usage-guide)
- [🛠️ Development](#️-development)
- [📝 Environment Variables](#-environment-variables)
- [🔍 Troubleshooting](#-troubleshooting)
- [👨‍💻 Authors](#-authors)
- [📄 License](#-license)

---

## ✨ Features

### 🎫 **Core CRM Functionality**
- **Support Ticket Management** - Create, view, update, and delete support tickets
- **Comment System** - Add and manage comments on tickets for collaboration
- **User Management** - Complete user lifecycle with roles and permissions
- **Dashboard** - Real-time overview of tickets, users, and system metrics

### 🔐 **Authentication & Security**
- **User Registration** - Self-service account creation with validation
- **Secure Login** - Session-based authentication with remember-me functionality
- **Role-based Access** - Admin and User roles with appropriate permissions
- **Demo Accounts** - Pre-configured admin and support accounts for testing

### 🎨 **Modern UI/UX**
- **Responsive Design** - Works seamlessly across desktop and mobile devices
- **Element Plus Components** - Professional UI components with consistent styling
- **Real-time Notifications** - Instant feedback for user actions
- **Animated Interface** - Smooth transitions and engaging visual effects

### 🐳 **Enterprise-Ready Deployment**
- **Full Docker Containerization** - Separate containers for frontend, backend, and database
- **Multi-environment Support** - Development and production configurations
- **Health Checks** - Automated service monitoring and recovery
- **Volume Persistence** - Data and logs are preserved across container restarts

---

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Database     │
│   (Vue.js)      │◄──►│  (Spring Boot)  │◄──►│    (MySQL)      │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 3306    │
│   Nginx Server  │    │   Tomcat        │    │   MySQL 8.0     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **Technology Stack**
- **Frontend:** Vue.js 3, Element Plus, Vue Router, Axios
- **Backend:** Spring Boot 2.7.2, Spring Data JPA, Hibernate
- **Database:** MySQL 8.0 with persistent volumes
- **Containerization:** Docker, Docker Compose
- **Build Tools:** Gradle (Backend), npm/Node.js (Frontend)

---

## 🚀 Quick Start

### **Prerequisites**
- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/)
- Git for cloning the repository

### **One-Command Setup**
```bash
# Clone and start the application
git clone https://github.com/spkdroid/TinyCRM.git
cd TinyCRM
docker-compose up -d
```

### **Access the Application**
- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **Database:** localhost:3306 (for external tools)

---

## � Development Setup

### **Development Environment**
```bash
# Start in development mode with live reload and debugging
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Access development features:
# - Backend debug port: 5005
# - Hot reload enabled
# - Detailed logging
```

### **Production Environment**
```bash
# Start in production mode with optimizations
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Production features:
# - Resource limits
# - Optimized builds
# - Security hardening
```

---

## 🔐 Authentication

### **User Registration**
New users can create accounts through the registration form:
- **Required Fields:** First Name, Last Name, Username, Email, Password
- **Validation:** Email format, password complexity, username uniqueness
- **Automatic Role:** New users get `USER` role by default

### **Login System**
- **Session-based Authentication** with secure session management
- **Remember Me** functionality for persistent sessions
- **Demo Accounts** available for testing:
  - Admin: `admin` / `admin123`
  - Support: `support` / `support123`

### **User Interface**
- **Tab-based Interface** - Switch between Sign In and Create Account
- **Real-time Validation** - Instant feedback on form fields
- **Responsive Design** - Works on all device sizes

---

## 📚 API Endpoints

### 🔐 **Authentication Endpoints**

#### User Registration
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "securePassword123"
}
```

#### User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123",
  "rememberMe": true
}
```

#### Logout
```http
POST /api/auth/logout
```

#### Get Current User
```http
GET /api/auth/me
```

### 👥 **User Management Endpoints**

#### Get All Users
```http
GET /api/users
```

#### Get User by ID
```http
GET /api/users/{id}
```

#### Create User (Admin)
```http
POST /api/users
Content-Type: application/json

{
  "username": "new_user",
  "email": "user@example.com",
  "firstName": "New",
  "lastName": "User",
  "password": "password123",
  "role": "USER"
}
```

#### Update User
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "firstName": "Updated",
  "lastName": "Name",
  "email": "updated@example.com"
}
```

#### Delete User
```http
DELETE /api/users/{id}
```

### 🎫 **Support Ticket Endpoints**

#### Create Support Ticket
```http
POST /api/tickets
Content-Type: application/json

{
  "title": "System Issue",
  "description": "Detailed description of the issue",
  "priority": "HIGH",
  "category": "TECHNICAL"
}
```

#### Get All Tickets
```http
GET /api/tickets
```

#### Get Ticket by ID
```http
GET /api/tickets/{id}
```

#### Update Ticket
```http
PUT /api/tickets/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS"
}
```

#### Delete Ticket
```http
DELETE /api/tickets/{id}
```

#### Get User's Tickets
```http
GET /api/tickets/user/{userId}
```

### 💬 **Comment Endpoints**

#### Add Comment to Ticket
```http
POST /api/comments
Content-Type: application/json

{
  "ticketId": 1,
  "content": "This is a comment on the ticket"
}
```

#### Get Comments for Ticket
```http
GET /api/comments/ticket/{ticketId}
```

#### Get All Comments
```http
GET /api/comments
```

#### Update Comment
```http
PUT /api/comments/{id}
Content-Type: application/json

{
  "content": "Updated comment content"
}
```

#### Delete Comment
```http
DELETE /api/comments/{id}
```

---

## 🐳 Docker Configuration

### **Service Architecture**
The application runs in three separate containers:

#### **Frontend Container** (`tinycrm-frontend`)
- **Base Image:** Node.js 18 → Nginx Alpine
- **Build Process:** Multi-stage build for optimized size
- **Port:** 3000 (mapped to container port 8080)
- **Features:** Static file serving, health checks, security headers

#### **Backend Container** (`tinycrm-backend`)
- **Base Image:** Gradle 8.5 → Eclipse Temurin 17 JRE
- **Build Process:** Multi-stage build with Gradle
- **Port:** 8080 (with debug port 5005 in dev mode)
- **Features:** JVM optimization, health monitoring, log volumes

#### **Database Container** (`tinycrm-database`)
- **Base Image:** MySQL 8.0
- **Initialization:** Automatic database and user creation
- **Port:** 3306
- **Features:** Persistent volumes, health checks, custom configuration

### **Docker Compose Files**

#### **docker-compose.yml** - Base Configuration
- Service definitions and networking
- Environment variable configuration
- Volume mounts and health checks

#### **docker-compose.dev.yml** - Development Overrides
- Debug port exposure (5005)
- Live reload capabilities
- Development environment variables
- Detailed logging

#### **docker-compose.prod.yml** - Production Overrides
- Resource limits and constraints
- Production optimizations
- Security hardening
- Log management

### **Environment Files**
- `.env` - Default environment variables
- `.env.dev` - Development-specific settings
- `.env.example` - Template for environment setup

---

## 🎯 Usage Guide

### **Getting Started**

1. **Register a New Account**
   - Navigate to http://localhost:3000
   - Click "Create Account" tab
   - Fill in your details and submit
   - Switch to "Sign In" tab and login

2. **Using Demo Accounts**
   - **Admin Account:** Username: `admin`, Password: `admin123`
   - **Support Account:** Username: `support`, Password: `support123`

3. **Creating Support Tickets**
   - Login to the dashboard
   - Click "File Ticket" or use the ticket creation form
   - Fill in title, description, priority, and category
   - Submit to create the ticket

4. **Managing Tickets**
   - View all tickets in the tickets list
   - Click on a ticket to view details
   - Add comments for collaboration
   - Update ticket status and priority as needed

### **Dashboard Features**
- **Ticket Overview:** View recent tickets and their status
- **User Statistics:** See user activity and registration metrics
- **Quick Actions:** Fast access to common tasks
- **Notifications:** Real-time updates on ticket activities

---

## 🛠️ Development

### **Local Development Setup**

#### **Backend Development**
```bash
# Navigate to backend directory
cd tinyOS

# Run with Gradle
./gradlew bootRun

# Run with debug mode
./gradlew bootRun --debug-jvm
```

#### **Frontend Development**
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run serve

# Build for production
npm run build
```

#### **Database Development**
```bash
# Connect to MySQL container
docker exec -it tinycrm-database mysql -u app_user -p support_module

# View logs
docker-compose logs database

# Backup database
docker exec tinycrm-database mysqldump -u app_user -papp_password support_module > backup.sql
```

### **Code Structure**

#### **Backend Structure** (`tinyOS/`)
```
src/main/java/com/spkd/tinycrm/tinyos/
├── controller/          # REST API controllers
├── service/            # Business logic services
├── repository/         # Data access layer
├── entity/            # JPA entities
├── dto/               # Data transfer objects
├── config/            # Configuration classes
└── TinyOsApplication.java
```

#### **Frontend Structure** (`frontend/src/`)
```
src/
├── components/         # Vue.js components
│   ├── LoginPage.vue  # Authentication
│   ├── Dashboard.vue  # Main dashboard
│   ├── TicketsList.vue # Ticket management
│   └── ...
├── router/            # Vue Router configuration
├── assets/            # Static assets
└── main.js           # Application entry point
```

---

## 📝 Environment Variables

### **Backend Environment Variables**
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/support_module
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=app_password

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=docker

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://frontend:8080

# Logging
LOGGING_LEVEL_COM_SPKD_TINYCRM=INFO
```

### **Frontend Environment Variables**
```bash
# API Configuration
VUE_APP_API_BASE_URL=http://localhost:8080
NODE_ENV=production
```

### **Database Environment Variables**
```bash
# MySQL Configuration
MYSQL_ROOT_PASSWORD=root_password
MYSQL_DATABASE=support_module
MYSQL_USER=app_user
MYSQL_PASSWORD=app_password
```

---

## 🔍 Troubleshooting

### **Common Issues**

#### **Port Conflicts**
```bash
# Check what's using the ports
netstat -tulpn | grep :3000
netstat -tulpn | grep :8080

# Kill processes using the ports
sudo kill -9 $(lsof -t -i:3000)
sudo kill -9 $(lsof -t -i:8080)
```

#### **Container Issues**
```bash
# View container logs
docker-compose logs [service-name]

# Restart specific service
docker-compose restart [service-name]

# Rebuild containers
docker-compose up --build

# Clean restart
docker-compose down
docker system prune -f
docker-compose up -d
```

#### **Database Connection Issues**
```bash
# Check database status
docker-compose ps database

# Test database connection
docker exec -it tinycrm-database mysql -u app_user -papp_password -e "SHOW DATABASES;"

# Reset database
docker-compose down -v
docker-compose up -d
```

### **Performance Optimization**

#### **Backend Optimization**
- **JVM Settings:** Already configured with container-optimized settings
- **Connection Pooling:** HikariCP configured for optimal performance
- **Logging:** Configurable levels for production vs development

#### **Frontend Optimization**
- **Bundle Size:** Webpack optimization for minimal bundle size
- **Caching:** Static assets cached with appropriate headers
- **Lazy Loading:** Routes and components loaded on demand

---

## 👨‍💻 Authors

- **Ramkumar Velmurugan**  
  [Portfolio](http://www.spkdroid.com/CV/) | [GitHub](https://github.com/spkdroid)

---

## � License

```
Copyright 2024 Ramkumar Velmurugan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made with ❤️ by [Ramkumar Velmurugan](https://github.com/spkdroid)

</div>
