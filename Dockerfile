# Builds the whole portal into one service: the React app is packed into the Spring Boot jar,
# so the website and the API share one address (no CORS or API address to configure). Used by render.yaml.

# 1. React app
FROM node:20-alpine AS frontend
WORKDIR /frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
RUN npm run build

# 2. Spring Boot API with the React build as static files
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY backend/pom.xml .
RUN mvn -q dependency:go-offline
COPY backend/src ./src
COPY --from=frontend /frontend/dist ./src/main/resources/static
RUN mvn -q -DskipTests package

# 3. Small runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
COPY backend/start.sh .
EXPOSE 8080
ENTRYPOINT ["./start.sh"]
