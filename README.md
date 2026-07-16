# Scalable Link Management API

A production-grade URL shortener with JWT authentication, built to explore backend design patterns like clean architecture, stateless auth, and scalable link handling.

## Tech Stack
- Java 21, Spring Boot 4.1
- Spring Security, JWT (JJWT 0.12.6)
- PostgreSQL, Spring Data JPA
- Maven

## Features
- Shorten long URLs into unique short codes (Base62 encoding)
- Redirect short codes to original URLs
- JWT-based user authentication (register/login)
- Passwords hashed with BCrypt
- Custom exception handling with clean error responses
- Planned to add more features as I go

## Setup

1. Clone the repo
2. Copy `application.properties.example` to `application.properties` and fill in your own values (DB credentials, JWT secret)
3. Create a PostgreSQL database matching your config
4. Run `mvn clean install`
5. Run `mvn spring-boot:run`
6. API available at `http://localhost:8080`
