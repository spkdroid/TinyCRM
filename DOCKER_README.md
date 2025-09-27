# TinyCRM Docker Setup

This document provides instructions for running TinyCRM using Docker containers with separate frontend and backend services.

## Architecture Overview

The application consists of three main services:
- **Frontend**: Vue.js application served by Nginx
- **Backend**: Spring Boot REST API
- **Database**: MySQL 8.0 database

## Prerequisites

- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose v3.8 or higher
- Git (for cloning the repository)

## Quick Start

### 1. Clone and Setup

```bash
# Clone the repository
git clone <repository-url>
cd TinyCRM

# Copy environment configuration
cp .env.example .env
```

### 2. Configure Environment

Edit the `.env` file with your preferred settings:

```bash
# Database Configuration
DB_ROOT_PASSWORD=your-secure-root-password
DB_NAME=support_module
DB_USER=app_user
DB_PASSWORD=your-secure-app-password
DB_PORT=3306

# Application Ports
BACKEND_PORT=8080
FRONTEND_PORT=3000

# Environment
SPRING_PROFILE=docker
LOG_LEVEL=INFO
```

### 3. Start the Application

#### Development Mode
```bash
# Using the management script (recommended)
./docker-manager.sh start-dev
# or
docker-manager.bat start-dev

# Or using docker-compose directly
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

#### Production Mode
```bash
# Using the management script (recommended)
./docker-manager.sh start-prod
# or
docker-manager.bat start-prod

# Or using docker-compose directly
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:3306 (for external tools)
- **Backend Health Check**: http://localhost:8080/actuator/health

## Management Scripts

We provide convenient management scripts for both Unix/Linux/Mac and Windows:

### Unix/Linux/Mac: `docker-manager.sh`
```bash
chmod +x docker-manager.sh

# Available commands
./docker-manager.sh build       # Build all images
./docker-manager.sh start-dev   # Start development environment
./docker-manager.sh start-prod  # Start production environment
./docker-manager.sh stop        # Stop all services
./docker-manager.sh restart     # Restart all services
./docker-manager.sh logs        # Show all logs
./docker-manager.sh logs backend # Show backend logs only
./docker-manager.sh health      # Check service health
./docker-manager.sh cleanup     # Remove everything (use with caution)
```

### Windows: `docker-manager.bat`
```cmd
REM Available commands (same as above)
docker-manager.bat start-dev
docker-manager.bat logs
docker-manager.bat health
```

## Docker Compose Files

- **docker-compose.yml**: Base configuration with all services
- **docker-compose.dev.yml**: Development overrides (debug ports, volume mounts)
- **docker-compose.prod.yml**: Production overrides (resource limits, optimized logging)

## Environment Configurations

- **.env.example**: Production environment template
- **.env.dev**: Development environment settings
- **.env**: Your local environment (create from example)

## Service Details

### Frontend Service (Vue.js + Nginx)

- **Port**: 3000 (configurable via FRONTEND_PORT)
- **Technology**: Vue.js 3 with Element Plus UI
- **Web Server**: Nginx Alpine
- **Features**:
  - Multi-stage build for optimized image size
  - Gzip compression enabled
  - Security headers configured
  - API proxy to backend service
  - Health check endpoint at `/health`

### Backend Service (Spring Boot)

- **Port**: 8080 (configurable via BACKEND_PORT)
- **Technology**: Spring Boot with Java 17
- **Features**:
  - JPA/Hibernate for database operations
  - Spring Security for authentication
  - Actuator endpoints for monitoring
  - CORS configuration for frontend integration
  - Health checks at `/actuator/health`
  - Debug port 5005 (development mode only)

### Database Service (MySQL)

- **Port**: 3306 (configurable via DB_PORT)
- **Technology**: MySQL 8.0
- **Features**:
  - Persistent volume for data storage
  - Custom configuration via mysql.cnf
  - Database initialization script
  - Health checks with mysqladmin

## Networking

All services communicate through a custom Docker network (`tinycrm-network`) with:
- Subnet: 172.20.0.0/16
- Service discovery by service name
- Isolated from other Docker networks

## Volumes

- **mysql_data**: Persistent MySQL data storage
- **backend_logs**: Application logs from Spring Boot

## Development Features

When running in development mode:
- Backend debug port exposed (5005)
- Detailed logging enabled
- All Actuator endpoints exposed
- Source code volume mounts for hot reloading (where applicable)

## Production Features

When running in production mode:
- Resource limits configured
- Optimized logging (rotated, limited size)
- Minimal Actuator endpoints exposed
- Security-focused configuration

## Troubleshooting

### Common Issues

1. **Port conflicts**: Change ports in `.env` file
2. **Database connection issues**: Check if MySQL service is healthy
3. **Frontend not loading**: Verify backend service is running and accessible

### Useful Commands

```bash
# Check service status
docker-compose ps

# View real-time logs
docker-compose logs -f [service-name]

# Execute commands in running containers
docker-compose exec backend bash
docker-compose exec database mysql -u root -p

# Rebuild specific service
docker-compose build --no-cache [service-name]

# Check resource usage
docker stats

# Clean up unused resources
docker system prune
```

### Health Checks

All services include health checks:
- **Database**: MySQL ping command
- **Backend**: Actuator health endpoint
- **Frontend**: Nginx health endpoint

Check health status:
```bash
# Using management script
./docker-manager.sh health

# Or manually
docker-compose ps
```

## Security Considerations

- Change default passwords in `.env` file
- Use strong passwords for production
- Keep Docker images updated
- Review exposed ports
- Use secrets management for production deployments

## Backup and Restore

### Database Backup
```bash
# Create backup
docker-compose exec database mysqldump -u root -p support_module > backup.sql

# Restore backup
docker-compose exec -T database mysql -u root -p support_module < backup.sql
```

### Volume Backup
```bash
# Backup MySQL data volume
docker run --rm -v tinycrm_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz -C /data .

# Restore MySQL data volume
docker run --rm -v tinycrm_mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql_backup.tar.gz -C /data
```

## Monitoring

Access monitoring endpoints:
- **Backend Health**: http://localhost:8080/actuator/health
- **Backend Metrics**: http://localhost:8080/actuator/metrics
- **Frontend Health**: http://localhost:3000/health

## Support

For issues and questions:
1. Check the logs: `docker-compose logs [service-name]`
2. Verify service health: `docker-compose ps`
3. Review this documentation
4. Check Docker and Docker Compose versions

---

**Note**: This setup is designed for development and small-scale production use. For enterprise deployments, consider using Kubernetes or other orchestration platforms.