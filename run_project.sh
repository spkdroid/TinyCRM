#!/bin/bash
# Shell script to run both backend (Spring Boot) and frontend (Vue.js) for TinyCRM

# Exit on error
set -e

# Start backend (tinyOS)
echo "Starting backend (Spring Boot)..."
cd tinyOS
# Detect OS and use appropriate Gradle wrapper
if [[ "$(uname -s)" == *NT* ]] || [[ "$(uname -o)" == *Msys* ]] || [[ "$(uname -o)" == *Cygwin* ]]; then
    # Windows environment
    ./gradlew.bat bootRun &
else
    # Unix-like environment
    if [ ! -x "./gradlew" ]; then
        chmod +x ./gradlew
    fi
    ./gradlew bootRun &
fi
BACKEND_PID=$!
cd ..

# Start frontend (Vue.js)
echo "Starting frontend (Vue.js)..."
cd frontend
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi
npm run serve &
FRONTEND_PID=$!
cd ..

echo "Both backend and frontend are starting."
echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo "Press Ctrl+C to stop both."

# Wait for both processes
wait $BACKEND_PID $FRONTEND_PID
