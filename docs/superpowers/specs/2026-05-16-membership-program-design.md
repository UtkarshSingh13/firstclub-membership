# FirstClub Membership Program — Design Spec

## Overview

Backend system for a FirstClub-style membership program offering subscription-based memberships with tiered benefits, integrated with a shopping/checkout flow. Inspired by FirstClub's Costco-model premium commerce platform.

The project must be easily runnable by anyone from GitHub with a single command.

## Tech Stack

- Java 21, Spring Boot 3.x, Maven
- Spring Data JPA, Hibernate
- H2 (default profile) / PostgreSQL (docker profile)
- Lombok, MapStruct
- springdoc-openapi (Swagger UI)
- Docker Compose (optional, for Postgres)

## Entity Design

### User

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| name | String | |
| email | String | Unique |
| phone | String | Nullable |
| cohort | String | e.g., "APARTMENT_BULK", "EARLY_ADOPTER". Used for cohort-based tier evaluation |
| createdAt | LocalDateTime | |

### MembershipPlan

Represents duration-based plans: Monthly, Quarterly, Yearly.

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| name | Enum (MONTHLY, QUARTERLY, YEARLY) | |
| durationInDays | Integer | 30, 90, 365 |
| price | BigDecimal | |
| active | Boolean | Soft-disable plans |

### MembershipTier

Represents Silver, Gold, Platinum tiers.

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| name | String | SILVER, GOLD, PLATINUM |
| rank | Integer | 1, 2, 3 — used for upgrade/downgrade comparison |
| description | String | |

### TierBenefit

Configurable perks per tier. Fully DB-driven.

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| tier | MembershipTier | FK |
| benefitType | Enum | CASHBACK_PERCENTAGE, FREE_DELIVERY_ABOVE, EXTRA_DISCOUNT_PERCENTAGE, EXCLUSIVE_DEALS_ACCESS, PRIORITY_SUPPORT, REVIEW_REWARD_MULTIPLIER |
| benefitValue | String | "5" for 5%, "2499" for free delivery above 2499, "true" for boolean perks |
| description | String | Human-readable description |

Seed data:

| Tier | Benefit | Value | Description |
|------|---------|-------|-------------|
| Silver | CASHBACK_PERCENTAGE | 2 | 2% cashback on every order |
| Silver | FREE_DELIVERY_ABOVE | 2499 | Free delivery on orders above Rs.2499 |
| Gold | CASHBACK_PERCENTAGE | 5 | 5% instant cashback on every order |
| Gold | FREE_DELIVERY_ABOVE | 999 | Free delivery on orders above Rs.999 |
| Gold | EXCLUSIVE_DEALS_ACCESS | true | Access to exclusive deals and early sales |
| Platinum | CASHBACK_PERCENTAGE | 10 | 10% cashback on every order |
| Platinum | FREE_DELIVERY_ABOVE | 0 | Always free delivery |
| Platinum | PRIORITY_SUPPORT | true | Priority customer support |
| Platinum | EXCLUSIVE_DEALS_ACCESS | true | Access to exclusive deals and early sales |
| Platinum | REVIEW_REWARD_MULTIPLIER | 2 | 2x review reward coins |

### TierCriteria

Configurable rules for automatic tier qualification. DB-driven.

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| tier | MembershipTier | FK |
| criteriaType | Enum | MIN_ORDER_COUNT, MIN_ORDER_VALUE, COHORT |
| thresholdValue | String | "5" for 5 orders, "10000" for Rs.10000, "EARLY_ADOPTER" for cohort |
| evaluationPeriodDays | Integer | Nullable. e.g., 30 for "in a month". Null for COHORT. |

Seed data:

| Tier | Criteria | Threshold | Period |
|------|----------|-----------|--------|
| Silver | — | Default tier on subscription | — |
| Gold | MIN_ORDER_COUNT | 5 | 30 |
| Gold | MIN_ORDER_VALUE | 10000 | 30 |
| Platinum | MIN_ORDER_COUNT | 15 | 30 |
| Platinum | MIN_ORDER_VALUE | 50000 | 30 |

Logic: Silver is default. For Gold/Platinum, meeting ANY one criteria qualifies (OR logic).

### UserSubscription

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| user | User | FK |
| plan | MembershipPlan | FK |
| tier | MembershipTier | FK |
| startDate | LocalDate | |
| endDate | LocalDate | Computed: startDate + plan.durationInDays |
| status | Enum | ACTIVE, CANCELLED, EXPIRED |
| version | Long | @Version — optimistic locking for concurrency |
| createdAt | LocalDateTime | |
| updatedAt | LocalDateTime | |

Constraints:
- One active subscription per user (enforced in service layer)
- Cancel sets status to CANCELLED; subscription remains until endDate
- Change-plan recalculates endDate from today

### Order (Stub)

| Field | Type | Notes |
|-------|------|-------|
| id | Long (auto) | PK |
| user | User | FK |
| amount | BigDecimal | |
| orderDate | LocalDateTime | |

## API Design

### Plan & Tier APIs (Read-only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/plans` | List all active membership plans |
| GET | `/api/tiers` | List all tiers with benefits and criteria |
| GET | `/api/tiers/{tierId}/benefits` | Benefits for a specific tier |

### User APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a user |
| GET | `/api/users/{userId}` | Get user with current subscription |

### Subscription APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/{userId}/subscriptions` | Subscribe (body: planName). Starts at Silver. |
| GET | `/api/users/{userId}/subscriptions/active` | Get active subscription with expiry and benefits |
| PUT | `/api/users/{userId}/subscriptions/upgrade-tier` | Manual tier upgrade (body: tierName) |
| PUT | `/api/users/{userId}/subscriptions/downgrade-tier` | Manual tier downgrade (body: tierName) |
| PUT | `/api/users/{userId}/subscriptions/change-plan` | Change plan duration (body: planName) |
| POST | `/api/users/{userId}/subscriptions/cancel` | Cancel subscription |

### Order APIs (Stub)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/{userId}/orders` | Place order (body: amount). Triggers tier evaluation. |
| GET | `/api/users/{userId}/orders` | List user's orders |

## Architecture

### Package Structure

```
com.firstclub.membership
├── controller/          # REST controllers
├── service/             # Business logic
├── repository/          # JPA repositories
├── entity/              # JPA entities
├── dto/                 # Request/Response DTOs
├── enums/               # BenefitType, CriteriaType, PlanName, SubscriptionStatus
├── strategy/            # Tier evaluation strategies
│   ├── TierEvaluationStrategy.java    (interface)
│   ├── OrderCountStrategy.java
│   ├── OrderValueStrategy.java
│   └── CohortStrategy.java
├── evaluator/           # Orchestrates strategy execution
│   └── TierEvaluator.java
├── exception/           # Custom exceptions + @RestControllerAdvice handler
├── config/              # App config, data seeder
└── mapper/              # Entity <-> DTO mappers (MapStruct)
```

### Strategy Pattern — Tier Evaluation

```
TierEvaluationStrategy (interface)
  + evaluate(userId: Long, criteria: TierCriteria) -> boolean

Implementations:
  - OrderCountStrategy: counts orders in evaluationPeriodDays, compares to threshold
  - OrderValueStrategy: sums order amounts in evaluationPeriodDays, compares to threshold
  - CohortStrategy: checks if user.cohort matches criteria.thresholdValue
```

**TierEvaluator** (orchestrator):
1. Loads all tiers ordered by rank descending (Platinum=3, Gold=2, Silver=1)
2. Iterates from highest rank downward; for each tier, checks if ANY criteria is satisfied (OR logic)
3. Returns the first (highest-ranked) qualifying tier
4. If qualifying tier rank > current subscription tier rank, upgrades the subscription automatically

Adding a new criteria type: implement `TierEvaluationStrategy`, add enum value to `CriteriaType`, register in evaluator. Zero changes to existing code.

### Concurrency

- `UserSubscription` uses `@Version` for optimistic locking
- If two concurrent order placements trigger tier evaluation simultaneously, the second will get an `OptimisticLockException` and can retry
- Order placement + tier evaluation runs in a single `@Transactional` boundary

### Error Handling

Global `@RestControllerAdvice` handles:
- `UserNotFoundException`
- `SubscriptionNotFoundException`
- `ActiveSubscriptionExistsException`
- `InvalidTierTransitionException` (e.g., downgrade via upgrade endpoint, or same tier)
- `SubscriptionAlreadyCancelledException`
- `PlanNotFoundException`

Returns consistent error response: `{ "error": "...", "message": "...", "timestamp": "..." }`

## Data Seeding

`DataSeeder` implements `CommandLineRunner`. Runs on every startup (idempotent — checks before inserting). Seeds:
- 3 plans (Monthly Rs.99, Quarterly Rs.249, Yearly Rs.799)
- 3 tiers (Silver, Gold, Platinum)
- 10 tier benefits (as per table above)
- 4 tier criteria (as per table above)

## Project Setup

### Default (H2 — zero setup)
```bash
mvn spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console
```

### With PostgreSQL
```bash
docker-compose up -d
mvn spring-boot:run -Dspring.profiles.active=docker
```

### docker-compose.yml
Provides a PostgreSQL 16 container with `firstclub` database.

## Demo Walkthrough

1. Start the app
2. Open Swagger UI
3. Create a user: `POST /api/users`
4. Browse plans: `GET /api/plans`
5. Browse tiers and benefits: `GET /api/tiers`
6. Subscribe to monthly plan: `POST /api/users/1/subscriptions` — starts at Silver
7. Check subscription: `GET /api/users/1/subscriptions/active` — shows Silver benefits
8. Place 5 orders of Rs.3000 each: `POST /api/users/1/orders` — after 5th order, auto-upgrades to Gold
9. Verify upgrade: `GET /api/users/1/subscriptions/active` — now Gold with 5% cashback
10. Manual downgrade: `PUT /api/users/1/subscriptions/downgrade-tier` with `{ "tierName": "SILVER" }`
11. Change plan: `PUT /api/users/1/subscriptions/change-plan` with `{ "planName": "YEARLY" }`
12. Cancel: `POST /api/users/1/subscriptions/cancel`

## Production Considerations (README mention only, not implemented)

- JWT-based authentication
- Scheduled job for periodic tier re-evaluation and subscription expiry
- Event-driven order integration (Kafka/SQS) instead of stub APIs
- Audit trail for subscription changes
- Rate limiting on APIs
- Caching for plans/tiers (rarely change)
