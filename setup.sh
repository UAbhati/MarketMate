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

# export all the variables in .env into the shell
set -o allexport
source .env
set +o allexport

# 2) Start Postgres only
echo "🐳 Starting Postgres…"
docker-compose up -d postgres

# 3) Run Spring Boot backend (bootRun) with DevTools
echo "🔧 Launching Spring Boot (dev mode)…"
# if gradlew wrapper exists in project root:
if [ -x "./gradlew" ]; then
  ./gradlew -p backend bootRun &
elif command -v gradle >/dev/null 2>&1; then
  gradle -p backend bootRun &
else
  echo "❌ Neither ./gradlew nor gradle found. Please install Gradle or add the wrapper." >&2
  exit 1
fi

# 4) Run Vite frontend
echo "🔧 Launching Vite dev server…"
(
  cd frontend
  npm install
  npm run dev
) &

# 5) Wait on both background jobs
wait
