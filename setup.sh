#!/usr/bin/env bash
set -euo pipefail

echo "📦 Setting up MarketMate…"

# 1) Ensure .env
if [ ! -f .env ]; then
  echo "Creating .env file…"
  cat > .env <<-EOL
POSTGRES_DB=marketmate
POSTGRES_USER=marketmate_user
POSTGRES_PASSWORD=marketmate_pass

SPRING_DATASOURCE_URL=jdbc:postgresql://marketmate-postgres:5432/marketmate
SPRING_DATASOURCE_USERNAME=marketmate_user
SPRING_DATASOURCE_PASSWORD=marketmate_pass
SPRING_JPA_HIBERNATE_DDL_AUTO=update

VITE_BACKEND_URL=http://localhost:8080
EOL
  echo ".env created ✅"
else
  echo ".env already exists ✅"
fi

# 2) Export .env vars into environment
set -o allexport
source .env
set +o allexport

# 3) Build and launch everything with Docker Compose
echo "🐳 Building & starting containers…"
docker-compose pull || true
docker-compose build
docker-compose up -d

# 4) Tail logs
echo "📜 Tailing logs (ctrl-c to quit)…"
docker-compose logs -f
