#!/bin/bash

echo "📦 Setting up MarketMate..."

# Step 1: Create .env if not exists
if [ ! -f .env ]; then
  echo "Creating .env file..."
  cat <<EOL > .env
POSTGRES_DB=marketmate
POSTGRES_USER=marketmate_user
POSTGRES_PASSWORD=marketmate_pass

SPRING_DATASOURCE_URL=jdbc:postgresql://marketmate-db:5432/marketmate
SPRING_DATASOURCE_USERNAME=marketmate_user
SPRING_DATASOURCE_PASSWORD=marketmate_pass
SPRING_JPA_HIBERNATE_DDL_AUTO=update

VITE_BACKEND_URL=http://localhost:8080
EOL
  echo ".env created ✅"
else
  echo ".env already exists ✅"
fi

# Step 2: Build backend
echo "🔧 Building Spring Boot backend..."
cd backend || exit
gradle build || { echo 'Gradle build failed ❌'; exit 1; }
cd ..

# Step 3: Start Docker containers using modern syntax
echo "🐳 Starting Docker containers..."
docker compose up --build
