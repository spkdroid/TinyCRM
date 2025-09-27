#!/bin/bash

# TinyCRM Docker Management Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to build all services
build_all() {
    print_status "Building all Docker images..."
    docker-compose build --no-cache
    print_success "All images built successfully!"
}

# Function to start services in development mode
start_dev() {
    print_status "Starting TinyCRM in development mode..."
    if [ ! -f .env ]; then
        print_warning ".env file not found. Copying from .env.dev..."
        cp .env.dev .env
    fi
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    print_success "TinyCRM development environment started!"
    print_status "Frontend: http://localhost:3000"
    print_status "Backend: http://localhost:8080"
    print_status "Database: localhost:3306"
}

# Function to start services in production mode
start_prod() {
    print_status "Starting TinyCRM in production mode..."
    if [ ! -f .env ]; then
        print_warning ".env file not found. Copying from .env.example..."
        cp .env.example .env
        print_warning "Please edit .env file with your production settings before starting."
        exit 1
    fi
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
    print_success "TinyCRM production environment started!"
    print_status "Frontend: http://localhost:3000"
    print_status "Backend: http://localhost:8080"
}

# Function to stop all services
stop_all() {
    print_status "Stopping all TinyCRM services..."
    docker-compose down
    print_success "All services stopped!"
}

# Function to view logs
show_logs() {
    local service=${1:-""}
    if [ -z "$service" ]; then
        print_status "Showing logs for all services..."
        docker-compose logs -f
    else
        print_status "Showing logs for $service..."
        docker-compose logs -f "$service"
    fi
}

# Function to clean up everything
cleanup() {
    print_warning "This will remove all containers, images, and volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        print_status "Cleaning up Docker resources..."
        docker-compose down -v --rmi all --remove-orphans
        docker system prune -f
        print_success "Cleanup completed!"
    else
        print_status "Cleanup cancelled."
    fi
}

# Function to check service health
health_check() {
    print_status "Checking service health..."
    echo ""
    
    # Check database
    if docker-compose ps database | grep -q "Up"; then
        print_success "Database: Running"
    else
        print_error "Database: Not running"
    fi
    
    # Check backend
    if docker-compose ps backend | grep -q "Up"; then
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "Backend: Running and healthy"
        else
            print_warning "Backend: Running but not responding"
        fi
    else
        print_error "Backend: Not running"
    fi
    
    # Check frontend
    if docker-compose ps frontend | grep -q "Up"; then
        if curl -f http://localhost:3000/health > /dev/null 2>&1; then
            print_success "Frontend: Running and healthy"
        else
            print_warning "Frontend: Running but not responding"
        fi
    else
        print_error "Frontend: Not running"
    fi
}

# Function to show usage
show_usage() {
    echo "TinyCRM Docker Management Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build       Build all Docker images"
    echo "  start-dev   Start in development mode"
    echo "  start-prod  Start in production mode"
    echo "  stop        Stop all services"
    echo "  restart     Restart all services"
    echo "  logs        Show logs for all services"
    echo "  logs <svc>  Show logs for specific service (database|backend|frontend)"
    echo "  health      Check health of all services"
    echo "  cleanup     Remove all containers, images, and volumes"
    echo "  help        Show this help message"
    echo ""
}

# Main script logic
case "${1:-help}" in
    build)
        check_docker
        build_all
        ;;
    start-dev)
        check_docker
        start_dev
        ;;
    start-prod)
        check_docker
        start_prod
        ;;
    stop)
        check_docker
        stop_all
        ;;
    restart)
        check_docker
        stop_all
        sleep 2
        start_dev
        ;;
    logs)
        check_docker
        show_logs "$2"
        ;;
    health)
        check_docker
        health_check
        ;;
    cleanup)
        check_docker
        cleanup
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        print_error "Unknown command: $1"
        show_usage
        exit 1
        ;;
esac