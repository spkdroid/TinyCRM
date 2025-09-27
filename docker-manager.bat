@echo off
REM TinyCRM Docker Management Script for Windows

setlocal enabledelayedexpansion

REM Function to check if Docker is running
:check_docker
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker and try again.
    exit /b 1
)
goto :eof

REM Function to build all services
:build_all
echo [INFO] Building all Docker images...
docker-compose build --no-cache
if errorlevel 1 (
    echo [ERROR] Failed to build images
    exit /b 1
)
echo [SUCCESS] All images built successfully!
goto :eof

REM Function to start services in development mode
:start_dev
echo [INFO] Starting TinyCRM in development mode...
if not exist .env (
    echo [WARNING] .env file not found. Copying from .env.dev...
    copy .env.dev .env
)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
if errorlevel 1 (
    echo [ERROR] Failed to start services
    exit /b 1
)
echo [SUCCESS] TinyCRM development environment started!
echo [INFO] Frontend: http://localhost:3000
echo [INFO] Backend: http://localhost:8080
echo [INFO] Database: localhost:3306
goto :eof

REM Function to start services in production mode
:start_prod
echo [INFO] Starting TinyCRM in production mode...
if not exist .env (
    echo [WARNING] .env file not found. Copying from .env.example...
    copy .env.example .env
    echo [WARNING] Please edit .env file with your production settings before starting.
    exit /b 1
)
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
if errorlevel 1 (
    echo [ERROR] Failed to start services
    exit /b 1
)
echo [SUCCESS] TinyCRM production environment started!
echo [INFO] Frontend: http://localhost:3000
echo [INFO] Backend: http://localhost:8080
goto :eof

REM Function to stop all services
:stop_all
echo [INFO] Stopping all TinyCRM services...
docker-compose down
echo [SUCCESS] All services stopped!
goto :eof

REM Function to show logs
:show_logs
if "%~2"=="" (
    echo [INFO] Showing logs for all services...
    docker-compose logs -f
) else (
    echo [INFO] Showing logs for %~2...
    docker-compose logs -f %~2
)
goto :eof

REM Function to check service health
:health_check
echo [INFO] Checking service health...
echo.

REM Check database
docker-compose ps database | find "Up" >nul
if errorlevel 1 (
    echo [ERROR] Database: Not running
) else (
    echo [SUCCESS] Database: Running
)

REM Check backend
docker-compose ps backend | find "Up" >nul
if errorlevel 1 (
    echo [ERROR] Backend: Not running
) else (
    curl -f http://localhost:8080/actuator/health >nul 2>&1
    if errorlevel 1 (
        echo [WARNING] Backend: Running but not responding
    ) else (
        echo [SUCCESS] Backend: Running and healthy
    )
)

REM Check frontend
docker-compose ps frontend | find "Up" >nul
if errorlevel 1 (
    echo [ERROR] Frontend: Not running
) else (
    curl -f http://localhost:3000/health >nul 2>&1
    if errorlevel 1 (
        echo [WARNING] Frontend: Running but not responding
    ) else (
        echo [SUCCESS] Frontend: Running and healthy
    )
)
goto :eof

REM Function to clean up everything
:cleanup
echo [WARNING] This will remove all containers, images, and volumes. Are you sure? (y/N)
set /p response=
if /i "!response!"=="y" (
    echo [INFO] Cleaning up Docker resources...
    docker-compose down -v --rmi all --remove-orphans
    docker system prune -f
    echo [SUCCESS] Cleanup completed!
) else (
    echo [INFO] Cleanup cancelled.
)
goto :eof

REM Function to show usage
:show_usage
echo TinyCRM Docker Management Script
echo.
echo Usage: %~nx0 [COMMAND]
echo.
echo Commands:
echo   build       Build all Docker images
echo   start-dev   Start in development mode
echo   start-prod  Start in production mode
echo   stop        Stop all services
echo   restart     Restart all services
echo   logs        Show logs for all services
echo   logs ^<svc^>  Show logs for specific service (database^|backend^|frontend)
echo   health      Check health of all services
echo   cleanup     Remove all containers, images, and volumes
echo   help        Show this help message
echo.
goto :eof

REM Main script logic
if "%~1"=="" goto show_usage
if "%~1"=="help" goto show_usage
if "%~1"=="--help" goto show_usage
if "%~1"=="-h" goto show_usage

call :check_docker

if "%~1"=="build" (
    call :build_all
) else if "%~1"=="start-dev" (
    call :start_dev
) else if "%~1"=="start-prod" (
    call :start_prod
) else if "%~1"=="stop" (
    call :stop_all
) else if "%~1"=="restart" (
    call :stop_all
    timeout /t 2 /nobreak >nul
    call :start_dev
) else if "%~1"=="logs" (
    call :show_logs %*
) else if "%~1"=="health" (
    call :health_check
) else if "%~1"=="cleanup" (
    call :cleanup
) else (
    echo [ERROR] Unknown command: %~1
    call :show_usage
    exit /b 1
)