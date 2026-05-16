# FirstClub Membership Program

Backend system for a membership program with tiered benefits.

## Tech Stack
- Java 21, Spring Boot, Maven
- Spring Data JPA, H2 in-memory database
- JUnit 5, Mockito

## How to Run

```bash
mvn spring-boot:run
```

Open http://localhost:8080/swagger-ui.html to explore APIs.

H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:firstclub`, user: `sa`)

## Run Tests

```bash
mvn test
```

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/plans | List membership plans |
| GET | /api/tiers | List tiers with benefits |
| GET | /api/tiers/{id}/benefits | Tier benefits |
| POST | /api/users | Create user |
| GET | /api/users/{id} | Get user |
| POST | /api/users/{id}/subscriptions | Subscribe (starts at Silver) |
| GET | /api/users/{id}/subscriptions/active | Active subscription |
| PUT | /api/users/{id}/subscriptions/upgrade-tier | Upgrade tier |
| PUT | /api/users/{id}/subscriptions/downgrade-tier | Downgrade tier |
| PUT | /api/users/{id}/subscriptions/change-plan | Change plan |
| POST | /api/users/{id}/subscriptions/cancel | Cancel |
| POST | /api/users/{id}/orders | Place order (triggers tier eval) |
| GET | /api/users/{id}/orders | List orders |

## Design

- **Strategy Pattern** for tier evaluation — each criteria type (order count, value, cohort) is a pluggable strategy
- **DB-driven config** — tier benefits and criteria stored in DB, not hardcoded
- **Optimistic locking** on subscriptions for concurrency safety
- Auto tier upgrade on order placement, manual upgrade/downgrade supported
