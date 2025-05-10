# MarketMate 🧠💹

MarketMate is a financial-domain chatbot web application that allows users to interact with LLMs to ask financial-market-related questions. It supports session-based conversations, real or mock LLM integration, financial data APIs, rate limits, and a modern frontend built with React and Tailwind.

---

## ✅ Features

### 💬 Chat Interface (Frontend)
- React-based UI with Tailwind styling.
- Session list and full chat window.
- Supports starting new sessions, resuming old ones.
- Model and tier-aware API calls (`useRealLLM`, `model`, `tier`).
- Auto-scroll, role-based message rendering, time-stamped messages.

### 🧠 LLM Support (Backend)
- Mock LLM replies for offline use.
- Real LLM support via [OpenRouter](https://openrouter.ai) using `deepseek/deepseek-chat-v3-0324:free`.
- API fallback for financial questions, filters for non-financial topics.

### 📈 Financial Intelligence
- Detects prompts like "PE ratio of Infosys", "quarterly results of TCS", etc.
- Provides mock API responses for:
  - Financial News
  - Quarterly Financial Results
  - Balance Sheet & Analyst Call

### 🚦 Rate Limiting
- Tier-based control:
  - `FREE`, `TIER_1`, `TIER_2`, `TIER_3`
- Based on requests/min, tokens/min, and daily limits.

---

## 🛠️ Tech Stack

### Backend
- Java 11
- Spring Boot 2.7.18
- Gradle
- PostgreSQL
- Swagger API Docs

### Frontend
- React 18
- Vite
- Tailwind CSS
- Axios
- React Router DOM
- clsx

---

## ⚙️ How to Run Locally

### 🔧 Backend

#### 1. Configure `.env` or `application.yml`
```env
OPENROUTER_API_TOKEN=your_openrouter_api_key
```

#### 2. Set up PostgreSQL
Ensure a `marketmate` database exists.

In `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/marketmate
    username: your_user
    password: your_pass
```

#### 3. Run the backend
```bash
cd backend
./gradlew bootRun
```

Swagger will be available at:
```
http://localhost:8080/swagger-ui.html
```

---

### 💻 Frontend

#### 1. Setup
```bash
cd frontend
npm install
```

#### 2. Create `.env` for frontend
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

#### 3. Run frontend
```bash
npm run dev
```

Visit:
```
http://localhost:5173
```

---

## 🧪 Testing Features

- Toggle `useRealLLM=true` in API calls to use OpenRouter
- Try:
  - `What is the PE ratio of Infosys?`
  - `Give me quarterly results for TCS`
  - `Hello` (triggers greeting logic)
  - `What is football?` (should be rejected)

---

## 🔒 Notes

- All queries are validated for financial domain before LLM processing.
- Responses are filtered to avoid non-financial content even after LLM reply.
- You can test freely using mock mode (`useRealLLM=false`).

---

## 📅 Future Improvements

- Real financial APIs (e.g., screener.in, Alpha Vantage)
- Auth, user management & subscription billing
- Frontend enhancements: avatars, animations, token usage tracking

---

_Made with ❤️ by ubaid
