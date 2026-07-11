#!/bin/bash

# Build and run user service with Docker

set -e

echo "Building Docker image..."
docker build -t user-service:latest .

echo "Running container..."
docker run -d --name user-service -p 8081:8081 user-service:latest

echo "User service is running on http://localhost:8081"
echo "API Docs: http://localhost:8081/swagger-ui.html"
echo "Health check: http://localhost:8081/actuator/health"

# Show logs
echo "Showing container logs (Ctrl+C to exit)..."
docker logs -f user-service