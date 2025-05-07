# Frontend Dockerfile (React + Vite + Tailwind)

# Stage 1: Build
FROM node:18-alpine as builder

WORKDIR /app
COPY . .
RUN npm install && npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
