
# Masar — Bus Ticket Booking API

Backend REST API for Masar, a bus ticket booking system. [Case Study for Elm]

## Tech Stack

- Java 17
- Spring Boot 4.1.0 (deviation from spec's 3.x — see note below)
- Maven
- Spring Web, Spring Data JPA, Spring Security
- H2 (in-memory, dev)
- Flyway (schema migrations)
- JWT (jjwt) for stateless authentication
- springdoc-openapi (Swagger UI)

### Note on Spring Boot version
The spec calls for Spring Boot 3.x, but 3.x is no longer available on Spring Initializr at the time of this project. Spring Boot 4.1.0 was used instead. This is a documented deviation, not an oversight — see the design note for more detail.

## Prerequisites

- Java 17 (JDK)
- Maven (or use the included wrapper — no separate install needed)
- No external database needed — H2 runs in-memory

## Running the Application

Clone the repository, then from the project root:

```bash
mvn spring-boot:run
```

Or, if Maven isn't installed globally, use the included wrapper:

```bash
./mvnw spring-boot:run
```
(On Windows: `mvnw.cmd spring-boot:run`)

The application starts on `http://localhost:8080`.

On startup, seed data is automatically created (see below). No manual database setup is required.

## Seed Data & Test Credentials

The application seeds the following data automatically on every startup (if not already present):

**Admin**

| Email | Password |
|---|---|
| admin@masar.com | admin123 |

**Operators** (each owns one bus company)

| Email | Password | Company |
|---|---|---|
| owner1@masar.com | operator123 | Sendalah Railways |
| owner2@masar.com | operator123 | Tuwaiq Transit Company |
| owner3@masar.com | operator123 | NEOM Rail Transport |

**Passengers** (with varying wallet balances)

| Email | Password | Wallet Balance |
|---|---|---|
| passenger1@masar.com | passenger123 | 1000.00 SAR |
| passenger2@masar.com | passenger123 | 300.00 SAR |
| passenger3@masar.com | passenger123 | 100.00 SAR (too low to afford a 4-seat booking at 150 SAR/seat) |

**Trips**: 10 trips are seeded across 4 cities (Riyadh, Hassa, Jeddah, Makkah), spread across the next 7 days, split across the 3 operators. All trips start with 40 total seats at 150.00 SAR/seat.

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

OpenAPI JSON: `http://localhost:8080/v3/api-docs`

To test authenticated endpoints in Swagger UI:
1. Call `POST /api/v1/auth/login` with one of the credentials above
2. Copy the `token` value from the response
3. Click the **Authorize** button (top right) and paste the token
4. All subsequent requests in Swagger UI will include the token automatically

## Postman Collection

`Masar.postman_collection.json` (project root) covers the happy path (login → search → book → view booking) plus the three required failure paths:
- Sold-out trip → `409 Conflict`
- Insufficient wallet balance → `422 Unprocessable Entity`
- Another passenger's booking (R8) → `404 Not Found` (not 403 — ownership is never revealed to a non-owner)

Import into Postman and run folders in order (1 → 2 → 3 → 4); each request has built-in test assertions on status codes, so "Run collection" gives a pass/fail summary for all 12 checks at once.

## Configuration & Profiles

Config is split across three files in `src/main/resources/`:
- `application.properties` — shared defaults (active profile, JWT settings, refund/booking business values)
- `application-dev.properties` — local development (H2 console enabled)
- `application-prod.properties` — production (H2 console disabled; `JWT_SECRET` environment variable is required with no fallback, so the app refuses to start with an insecure default secret)

The active profile defaults to `dev`, so running the app normally needs no extra setup. Refund thresholds (24h/2h tiers and their percentages), the booking cutoff (30 minutes before departure), and seat limits are externalized as properties rather than hardcoded.

## Clone to First Successful Booking — Step by Step

1. Clone the repo and run `mvn spring-boot:run`
2. Wait for the console to show `Seed data created...` — confirms the app is ready
3. Log in as a passenger with funds:
   ```
   POST /api/v1/auth/login
   { "email": "passenger1@masar.com", "password": "passenger123" }
   ```
4. Copy the returned `token`
5. Search for a trip:
   ```
   GET /api/v1/trips
   ```
6. Pick a `SCHEDULED` trip's `id` from the response
7. Book it (using the token as a Bearer token in the Authorization header):
   ```
   POST /api/v1/bookings
   { "tripId": <id from step 6>, "seatCount": 1 }
   ```
8. A `201 Created` response with a `CONFIRMED` booking, reference code, and ticket confirms success.

## H2 Console (for inspecting the dev database directly)

`http://localhost:8080/h2-console`

Since H2 runs in-memory, the JDBC URL changes on every restart. Find the current URL in the startup console log, under a line like:
```
Database JDBC URL [jdbc:h2:mem:xxxxxxxx-...]
```
Username: `SA`, no password.

## Running Tests

```bash
mvn test
```

Or with the wrapper:
```bash
./mvnw test
```

Test coverage includes:
- Unit tests covering every R5 refund tier boundary (exactly 24h, exactly 2h, and both sides of each)
- Unit tests covering R1 seat availability logic
- Integration tests (MockMvc/@SpringBootTest) covering unauthenticated access rejection and R8 ownership enforcement

## Deliverables Checklist (per case study Section 6)

- [x] Source code — public Git repository, incremental commit history
- [x] Runs locally via `mvn spring-boot:run` against H2
- [x] Seed data — 1 admin, 3 operators, 4 cities, 10 trips across the next 7 days, 3 passengers with different wallet balances (including one too low for a 4-seat booking)
- [x] API docs — Swagger UI with example request/response bodies
- [x] Design note — see `DESIGN_NOTE.md`
- [x] Tests — green on a clean clone

## Project Structure

```
src/main/java/com/maryam/masar/
├── config/          # DataSeeder, OpenApiConfig
├── controller/      # REST controllers
├── dto/             # Request/response DTOs
├── entity/          # JPA entities
├── exception/       # Custom exceptions + global handler
├── repository/      # Spring Data repositories
├── security/        # JWT, Spring Security config
├── service/         # Business logic
└── validation/      # Custom Bean Validation constraints

src/main/resources/
├── db/migration/    # Flyway SQL migrations
└── application.properties

src/test/java/com/maryam/masar/
├── service/          # Unit tests
└── BookingIntegrationTest.java  # Integration tests
```