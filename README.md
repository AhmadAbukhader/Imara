# Imara

Multi-tenant apartment billing management system. Each company manages buildings, apartments, service types, and bills for residential properties.

---

## Overview

Imara enables property management companies to:

- Manage multiple buildings and apartments
- Define service types (water, gas, electricity, etc.) and assign them to buildings with cost and billing period
- Track apartment owners/residents and their apartment assignments
- Assign MAINTAINERs (apartment owners) to manage specific buildings
- Generate and track bills per apartment, service, and billing period
- Handle optional service subscriptions per apartment

---

## Tech Stack

| Category     | Technology                    |
|--------------|-------------------------------|
| Language     | Java 17                       |
| Framework    | Spring Boot 4.0.3             |
| Database     | PostgreSQL                    |
| Data Access  | Spring Data JDBC              |
| Migrations   | Flyway                        |
| Security     | Spring Security + JWT         |
| Validation   | Jakarta Validation            |
| Build        | Maven                         |

---

## Prerequisites

- **Java 17**
- **Maven 3.8+**
- **PostgreSQL 14+**

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/imara.git
cd imara
```

### 2. Database setup

Create a PostgreSQL database:

```sql
CREATE DATABASE imara;
```

Flyway will create the schema and tables on first run (see `src/main/resources/db/migration/`).

### 3. Configure

Create or edit `src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/imara?currentSchema=imara_schema
spring.datasource.username=postgres
spring.datasource.password=<your-password>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.flyway.enabled=true
spring.flyway.schemas=imara_schema

jwt.secret=<at-least-32-character-secret>
jwt.expiration-ms=86400000
```

For production, use environment variables for sensitive values.

### 4. Run

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8089` (default port).

---

## API Overview

| Endpoint                    | Method | Description                      |
|----------------------------|--------|----------------------------------|
| `/api/auth/login`          | POST   | Login, returns JWT               |
| `/api/auth/me`             | GET    | Current user info (auth required)|
| `/api/auth/register`       | POST   | Company owner signup             |
| `/api/auth/register/join`  | POST   | Apartment owner join company     |

All `/api/**` endpoints except auth require a valid JWT in the `Authorization: Bearer <token>` header.

See [API_README.md](./API_README.md) for full API specification and implementation status.

---

## Project Structure

```
src/main/java/com/Imara/imara/
├── ImaraApplication.java          # Entry point
├── config/                        # Security & app configuration
├── controller/                    # REST controllers
│   └── dto/                       # Request DTOs
├── dto/                           # Response DTOs
├── exception/                     # ApplicationException, ErrorCode
│   ├── ExceptionMapper.java      # Maps Spring DB exceptions → ApplicationException
│   └── handler/
│       └── ApiExceptionHandler.java   # Global exception → HTTP response
├── model/                         # Domain entities
├── repository/                    # Data access interfaces (I*Repository)
│   └── impl/                      # Repository implementations
├── security/                      # JWT, filters, UserPrincipal
└── service/                       # Business logic interfaces (I*Service)
    └── impl/                      # Service implementations
```

---

## Architecture

- **Interface + implementation pattern:** `IUserService` / `UserService`, `IUserRepository` / `UserRepository`. Interfaces are injected in controllers and services.
- **Naming:** Interfaces use `I` prefix (e.g. `IUserService`); implementations use the base name (e.g. `UserService`).
- **Layers:**
  - **Controller:** No business logic; delegates to services.
  - **Service:** Business logic and validation; throws `ApplicationException` with `ErrorCode`.
  - **Repository:** Database access only; uses `ExceptionMapper` to convert Spring exceptions.
- **Exception handling:** Single `ApplicationException` + `ErrorCode` enum; `ApiExceptionHandler` maps to HTTP responses. `ExceptionMapper` converts Spring `DataAccessException` to `ApplicationException`.

---

## User Roles

| Role           | Description                                                |
|----------------|------------------------------------------------------------|
| COMPANY_OWNER  | One per company; manages company, buildings, users, bills  |
| MAINTAINER     | Apartment owner promoted to manage a building              |

---

## License

[Add your license here]
