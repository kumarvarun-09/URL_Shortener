# URL Shortener

A production-style URL shortener built using Java, Spring Boot, Redis, PostgreSQL, and Docker.

This project focuses on backend engineering concepts such as caching, rate limiting, analytics tracking, scheduled jobs, containerized deployment, and externalized configuration.

---

# Features

- URL shortening using Base62 encoding
- URL redirection
- Redis caching for fast lookups
- PostgreSQL persistence
- Database fallback in case Redis is unavailable
- Click analytics tracking
- Scheduled analytics synchronization job
- IP-based rate limiting
- Dockerized deployment
- Swagger/OpenAPI documentation
- Spring Boot Actuator monitoring
- Externalized configuration using Spring profiles and environment variables
- Structured logging

---

# Tech Stack

## Backend
- Java
- Spring Boot
- Spring Data JPA
- Spring Data Redis

## Database & Cache
- PostgreSQL
- Redis

## DevOps & Deployment
- Docker
- Docker Compose

## Build Tool
- Maven

---

# Architecture

```text
                ┌─────────────────┐
                │     Client      │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ Spring Boot App │
                └───────┬─────────┘
                        │
         ┌──────────────┴──────────────┐
         ▼                             ▼
 ┌───────────────┐             ┌────────────────┐
 │     Redis     │             │   PostgreSQL   │
 │   (Caching)   │             │  (Persistence) │
 └───────────────┘             └────────────────┘
```

---

# API Documentation

Swagger UI:

```text
http://localhost:8090/swagger-ui.html
```

---

# Important Endpoints

## Create Short URL

```http
POST /url/shorten
```

Request Body:

```json
{
  "url": "https://google.com"
}
```

Response Example:

```json
{
  "shortUrl": "http://localhost:8090/url/abc123"
}
```

---

## Redirect URL

```http
GET /url/{shortCode}
```

Example:

```text
GET /url/abc123
```

Redirects to the original URL.

---

# Running the Project

## Clone Repository

```bash
git clone https://github.com/kumarvarun-09/URL_Shortener.git
cd URL_Shortener
```

---

## Run Using Docker

```bash
docker compose up --build
```

Application runs on:

```text
http://localhost:8090
```

---

# Environment Variables

```env
SPRING_PROFILES_ACTIVE=prod

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/url_shortener
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

APP_BASE_URL=http://localhost:8090/url/
```

---

# Monitoring

Actuator health endpoint:

```text
http://localhost:8090/actuator/health
```

---

# Logging

- Console logging enabled
- File-based logging enabled
- Rolling log policy configured

---

# Docker Setup

The application runs using three separate containers:

- Spring Boot Application
- PostgreSQL
- Redis

Docker Compose is used for orchestration and container networking.

---

# Production Concepts Implemented

- Redis caching
- Cache TTL management
- Null caching strategy
- Database fallback strategy
- Scheduled background jobs
- Rate limiting using Redis
- Externalized configuration
- Docker networking
- Persistent PostgreSQL volumes
- Health monitoring using Actuator
- API documentation using Swagger

---

# Challenges Faced During Development

- Redis connectivity issues between containers
- Docker container networking configuration
- PostgreSQL volume compatibility issues
- Environment variable configuration inside Docker containers
- Container-to-container communication debugging
- Spring profile management for Docker deployment

---

# Future Improvements

- Flyway database migrations
- Integration tests
- CI/CD pipeline
- Distributed locking
- Custom short URLs
- Link expiry support

---

# Learning Outcomes

This project helped in understanding:

- Spring Boot backend development
- Redis caching strategies
- PostgreSQL integration
- Docker and Docker Compose
- Production-style configuration management
- Backend debugging and deployment workflows
- API documentation using Swagger
- Basic observability using Spring Boot Actuator

---

# Author

Varun Kumar
