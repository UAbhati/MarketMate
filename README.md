# MarketMate

MarketMate is a financial AI assistant chat interface built using a modern full-stack architecture.

## 🔧 Tech Stack

### Backend:
- **Spring Boot** – Java-based RESTful API backend
- **Spring Security + JWT** – Secure stateless authentication
- **PostgreSQL** – Reliable relational DB for usage and user data

### Frontend:
- **React + Vite** – Fast, modern, and developer-friendly UI
- **Tailwind CSS** – Utility-first CSS framework

## ✅ Requirements

Before starting, ensure the following tools are installed:

- [Java 21+](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop) (with Docker Compose v2)
- [Git](https://git-scm.com/)
- (Optional) [Gradle](https://gradle.org/) if not using the wrapper

## 🚀 Quick Setup Using Script

1. Clone the repo:
```bash
git clone https://github.com/yourname/marketmate.git
cd marketmate
```

2. Run the setup script:
```bash
bash setup.sh
```

This will:
- Create a `.env` file with DB + backend config
- Build the Spring Boot backend
- Start Docker containers: PostgreSQL, backend, frontend

3. Visit the app:
- Frontend: [http://localhost:3000](http://localhost:3000)
- API Docs: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🛠️ Manual Setup (If needed)

### 1. Backend (Spring Boot)
```bash
cd backend
gradle build
```
Update `application.yml` or use `.env` to connect to PostgreSQL.

### 2. Frontend (React + Tailwind)
```bash
cd frontend
npm install
npm run dev
```

## 🔐 Authentication
- Email/password login (`/auth/login`)
- JWT token stored in frontend
- Secure access to `/chat` routes

## 🐳 Docker Setup

### `docker-compose.yml` runs:
- PostgreSQL on port `5432`
- Spring Boot on port `8080`
- Nginx serving frontend on port `3000`

To run:
```bash
docker compose up --build
```

## ✨ Features
- JWT login & registration
- Tier-based API usage limits
- Daily request/token usage tracking
- Swagger docs enabled
- Production-ready Dockerfiles

---

Built with ❤️ by Ubaid using Spring, React, and AI.
