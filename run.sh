#!/bin/bash

# TinyCRM Setup and Run Script
# This script installs dependencies and runs the TinyCRM project

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored output
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

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if Docker is running
is_docker_running() {
    docker info >/dev/null 2>&1
}

# Function to run with Docker (recommended)
run_with_docker() {
    print_status "Starting TinyCRM with Docker..."
    
    # Check if Docker is installed
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker Desktop for macOS:"
        print_error "https://docs.docker.com/desktop/mac/install/"
        exit 1
    fi
    
    # Check if Docker Compose is available
    if ! command_exists docker-compose && ! docker compose version >/dev/null 2>&1; then
        print_error "Docker Compose is not available. Please install Docker Desktop which includes Docker Compose."
        exit 1
    fi
    
    # Check if Docker is running
    if ! is_docker_running; then
        print_error "Docker is not running. Please start Docker Desktop."
        exit 1
    fi
    
    print_status "Building and starting containers..."
    
    # Use docker compose (newer) or docker-compose (legacy)
    if docker compose version >/dev/null 2>&1; then
        DOCKER_COMPOSE_CMD="docker compose"
    else
        DOCKER_COMPOSE_CMD="docker-compose"
    fi
    
    # Build and start all services
    $DOCKER_COMPOSE_CMD up --build -d
    
    print_success "All services are starting up!"
    print_status "Waiting for services to be ready..."
    
    # Wait a bit for services to start
    sleep 10
    
    # Check service status
    print_status "Service status:"
    $DOCKER_COMPOSE_CMD ps
    
    print_success "TinyCRM is now running!"
    echo ""
    echo "Access your application:"
    echo "  🌐 Frontend (Vue.js): http://localhost:3000"
    echo "  🚀 Backend (Spring Boot): http://localhost:8080"
    echo "  🗄️  MySQL Database: localhost:3306"
    echo ""
    echo "To stop the application:"
    echo "  $DOCKER_COMPOSE_CMD down"
    echo ""
    echo "To view logs:"
    echo "  $DOCKER_COMPOSE_CMD logs -f"
}

# Function to run locally (for development)
run_locally() {
    print_status "Setting up TinyCRM for local development..."
    
    # Check prerequisites
    print_status "Checking prerequisites..."
    
    # Check Java
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 11 or higher."
        print_error "You can install it using Homebrew: brew install openjdk@11"
        exit 1
    fi
    
    # Check Node.js
    if ! command_exists node; then
        print_error "Node.js is not installed. Please install Node.js."
        print_error "You can install it using Homebrew: brew install node"
        exit 1
    fi
    
    # Check npm
    if ! command_exists npm; then
        print_error "npm is not installed. Please install npm (usually comes with Node.js)."
        exit 1
    fi
    
    # Check MySQL (optional warning)
    if ! command_exists mysql; then
        print_warning "MySQL is not installed locally. You'll need to either:"
        print_warning "1. Install MySQL: brew install mysql"
        print_warning "2. Use Docker for MySQL only: docker run -d -p 3306:3306 --name mysql-tinycrm -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=support_module -e MYSQL_USER=app_user -e MYSQL_PASSWORD=app_password mysql:5.7"
        print_warning "3. Use the Docker setup instead (recommended)"
    fi
    
    print_success "Prerequisites check completed!"
    
    # Install frontend dependencies
    print_status "Installing frontend dependencies..."
    cd frontend
    npm install
    npm audit fix --force 2>/dev/null || true
    cd ..
    print_success "Frontend dependencies installed!"
    
    # Build backend
    print_status "Building backend application..."
    cd tinyOS
    if command_exists ./gradlew; then
        ./gradlew build -x test
    elif command_exists gradle; then
        gradle build -x test
    else
        print_error "Gradle is not available. Please install Gradle or use Docker setup."
        exit 1
    fi
    cd ..
    print_success "Backend application built!"
    
    # Instructions for running
    print_success "Setup completed!"
    echo ""
    echo "To run the application locally:"
    echo ""
    echo "1. Start MySQL database (if not already running):"
    echo "   mysql.server start"
    echo "   # Or use Docker: docker run -d -p 3306:3306 --name mysql-tinycrm -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=support_module -e MYSQL_USER=app_user -e MYSQL_PASSWORD=app_password mysql:5.7"
    echo ""
    echo "2. Start the backend (in a new terminal):"
    echo "   cd tinyOS && ./gradlew bootRun"
    echo ""
    echo "3. Start the frontend (in another terminal):"
    echo "   cd frontend && npm run serve"
    echo ""
    echo "Then access:"
    echo "  🌐 Frontend: http://localhost:8080 (Vue dev server)"
    echo "  🚀 Backend: http://localhost:8080 (Spring Boot)"
}

# Function to show help
show_help() {
    echo "TinyCRM Setup and Run Script"
    echo ""
    echo "Usage: $0 [option]"
    echo ""
    echo "Options:"
    echo "  docker    Run with Docker (recommended) - default"
    echo "  local     Set up for local development"
    echo "  stop      Stop Docker containers"
    echo "  logs      Show Docker container logs"
    echo "  clean     Clean up Docker containers and images"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0              # Run with Docker (default)"
    echo "  $0 docker       # Run with Docker"
    echo "  $0 local        # Set up for local development"
    echo "  $0 stop         # Stop all containers"
    echo "  $0 logs         # Show logs"
}

# Function to stop containers
stop_containers() {
    print_status "Stopping TinyCRM containers..."
    
    if command_exists docker-compose; then
        docker-compose down
    elif docker compose version >/dev/null 2>&1; then
        docker compose down
    else
        print_error "Docker Compose not found"
        exit 1
    fi
    
    print_success "Containers stopped!"
}

# Function to show logs
show_logs() {
    print_status "Showing container logs..."
    
    if command_exists docker-compose; then
        docker-compose logs -f
    elif docker compose version >/dev/null 2>&1; then
        docker compose logs -f
    else
        print_error "Docker Compose not found"
        exit 1
    fi
}

# Function to clean up
clean_up() {
    print_status "Cleaning up TinyCRM containers and images..."
    
    if command_exists docker-compose; then
        DOCKER_COMPOSE_CMD="docker-compose"
    elif docker compose version >/dev/null 2>&1; then
        DOCKER_COMPOSE_CMD="docker compose"
    else
        print_error "Docker Compose not found"
        exit 1
    fi
    
    $DOCKER_COMPOSE_CMD down --rmi all --volumes --remove-orphans
    
    print_success "Cleanup completed!"
}

# Main script logic
main() {
    echo "🚀 TinyCRM Setup and Run Script"
    echo "=================================="
    echo ""
    
    case "${1:-docker}" in
        "docker"|"")
            run_with_docker
            ;;
        "local")
            run_locally
            ;;
        "stop")
            stop_containers
            ;;
        "logs")
            show_logs
            ;;
        "clean")
            clean_up
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            print_error "Unknown option: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
