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

## Testing Walkthrough

Follow these steps in order to test the full membership lifecycle. All commands use `curl` — run the app first with `mvn spring-boot:run`.

### 1. View available plans and tiers

```bash
# See membership plans (Monthly ₹99, Quarterly ₹249, Yearly ₹799)
curl http://localhost:8080/api/plans

# See tiers and their benefits (Silver, Gold, Platinum)
curl http://localhost:8080/api/tiers
```

### 2. Create a user

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Rahul Sharma", "email": "rahul@example.com", "phone": "9876543210"}'
```

Note the `id` in the response (should be `1`). Use it in all subsequent calls.

### 3. Subscribe to a plan (starts at Silver tier)

```bash
curl -X POST http://localhost:8080/api/users/1/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"planName": "MONTHLY"}'
```

The user is now a Silver member on the Monthly plan.

### 4. Check active subscription

```bash
curl http://localhost:8080/api/users/1/subscriptions/active
```

### 5. Place orders to trigger auto tier upgrade

Placing orders triggers automatic tier evaluation. Gold requires **5 orders** or **₹10,000 total order value** in the last 30 days.

```bash
# Place 5 orders (each ₹500) — this will trigger upgrade to Gold on the 5th order
curl -X POST http://localhost:8080/api/users/1/orders -H "Content-Type: application/json" -d '{"amount": 500}'
curl -X POST http://localhost:8080/api/users/1/orders -H "Content-Type: application/json" -d '{"amount": 500}'
curl -X POST http://localhost:8080/api/users/1/orders -H "Content-Type: application/json" -d '{"amount": 500}'
curl -X POST http://localhost:8080/api/users/1/orders -H "Content-Type: application/json" -d '{"amount": 500}'
curl -X POST http://localhost:8080/api/users/1/orders -H "Content-Type: application/json" -d '{"amount": 500}'
```

Now check the subscription — tier should be **GOLD**:

```bash
curl http://localhost:8080/api/users/1/subscriptions/active
```

### 6. Auto upgrade to Platinum via order value

Platinum requires **15 orders** or **₹50,000 total value** in 30 days. Place a big order:

```bash
curl -X POST http://localhost:8080/api/users/1/orders \
  -H "Content-Type: application/json" \
  -d '{"amount": 48000}'
```

Total order value is now ₹50,500 (5×500 + 48,000). Check subscription — should be **PLATINUM**:

```bash
curl http://localhost:8080/api/users/1/subscriptions/active
```

### 7. Manual tier downgrade

```bash
curl -X PUT http://localhost:8080/api/users/1/subscriptions/downgrade-tier \
  -H "Content-Type: application/json" \
  -d '{"tierName": "GOLD"}'
```

### 8. Manual tier upgrade

```bash
curl -X PUT http://localhost:8080/api/users/1/subscriptions/upgrade-tier \
  -H "Content-Type: application/json" \
  -d '{"tierName": "PLATINUM"}'
```

### 9. Change plan

```bash
curl -X PUT http://localhost:8080/api/users/1/subscriptions/change-plan \
  -H "Content-Type: application/json" \
  -d '{"planName": "YEARLY"}'
```

### 10. View order history

```bash
curl http://localhost:8080/api/users/1/orders
```

### 11. Cancel subscription

```bash
curl -X POST http://localhost:8080/api/users/1/subscriptions/cancel
```

### Tier Criteria Summary

| Tier | Min Orders (30 days) | Min Order Value (30 days) |
|------|---------------------|--------------------------|
| Silver | — (default) | — |
| Gold | 5 | ₹10,000 |
| Platinum | 15 | ₹50,000 |

Criteria use **OR** logic — meeting any single criteria qualifies for that tier.

## Design

- **Strategy Pattern** for tier evaluation — each criteria type (order count, value, cohort) is a pluggable strategy
- **DB-driven config** — tier benefits and criteria stored in DB, not hardcoded
- **Optimistic locking** on subscriptions for concurrency safety
- Auto tier upgrade on order placement, manual upgrade/downgrade supported
