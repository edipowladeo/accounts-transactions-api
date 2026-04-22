# Account & Transactions API

REST API for managing customer accounts and financial transactions.

## Overview

This service allows:

- Creating accounts associated with a document number
- Registering financial transactions for an account
- Retrieving account balance by summing all persisted transactions

Each transaction has an operation type:

- PURCHASE
- INSTALLMENT_PURCHASE
- WITHDRAWAL
- PAYMENT

### Business Rules

- Debit operations (purchase, installment, withdrawal) are stored as **negative amounts**
- Payments are stored as **positive amounts**
- Account balance is computed as the **sum of all transaction amounts** for an account

---


## Tech Stack

- **Java** with Spring Boot
- **H2** file database for persistence
- **Docker** with a named volume to persist H2 data across container restarts
- **Swagger/OpenAPI** for API documentation

## Running with Docker

### Using Docker Compose (recommended)

```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`.

The H2 database file is stored in the `/data` volume, so data is preserved even if the container is restarted.

To stop:

```bash
docker-compose down
```


## Running locally

## Requirements

- Java 21+
- Gradle (or use the Gradle wrapper included in the project)

- ### Using the Gradle Wrapper (recommended)

```bash
./gradlew bootRun
```

### Building and running the JAR

```bash
./gradlew build
java -jar build/libs/ledger-*.jar
```

## Running Tests

```bash
./gradlew test
```

The generated JAR will be available at `build/libs/`.


# API Documentation

Once running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

## Balance endpoint

### Get account balance

`GET /balance?accountId={id}`

Returns the current balance for the informed account by summing all values from the `transactions` table.

#### Success response (`200`)

```json
{
  "account_id": 1,
  "balance": 23.45
}
```

#### Error semantics

- `400 INVALID_PARAMETER` for invalid `accountId` value or type
- `400 INVALID_REQUEST` when `accountId` query parameter is missing
- `404 ACCOUNT_NOT_FOUND` when account does not exist
