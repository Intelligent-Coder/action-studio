#!/bin/bash

# Stop and remove user service container

echo "Stopping user service..."
docker stop user-service 2>/dev/null || true

echo "Removing container..."
docker rm user-service 2>/dev/null || true

echo "User service stopped and removed."