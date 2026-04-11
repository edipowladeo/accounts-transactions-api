# Pismo Account & Transactions API

REST API for managing customer accounts and financial transactions.

## Overview

The service allows creating accounts tied to a document number and registering transactions against them. Each transaction has an operation type (purchase, installment purchase, withdrawal, or payment). 
Debit operations are stored with negative amounts, payments with positive amounts.

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
