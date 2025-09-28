# RedBus - Bus Booking System

- [Architecture Plan](https://docs.google.com/document/d/1_mpcazFbCPB8nM-c_hjhbnwDTgyHA6wXv8Cvv75WUk0/edit?usp=sharing)
- [API Documentation](https://github.com/rtwkprojs/redbus-service/blob/main/API_DOCUMENTATION.md)

## Architecture

### Services
- **User Service** (8081): Authentication and user management
- **Agency Service** (8082): Bus agency and vehicle management  
- **Journey Service** (8083): Routes, stops, and journey scheduling
- **Booking Service** (8084): Booking orchestration with seat locking
- **Payment Service** (8086): Payment processing (mock implementation)

### Technology Stack
- Java 17
- Spring Boot 3.4.10
- PostgreSQL 14
- Docker & Docker Compose
- Maven

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17 (for local development)
- Maven 3.8+

### Running with Docker

1. Build and start all services:
```bash
docker-compose up --build
```

2. Wait for all services to be healthy (check logs)

3. Load test data:
```bash
./load-test-data.sh
```

### Running Locally

1. Start PostgreSQL:
```bash
docker run -d --name postgres-redbus \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=redbus \
  -p 5433:5432 \
  postgres:14
```

2. Start services individually:
```bash
# In separate terminals
cd user-service && mvn spring-boot:run
cd agency-service && mvn spring-boot:run
cd journey-service && mvn spring-boot:run
cd booking-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
```

3. Load test data:
```bash
./load-test-data.sh
```

## Testing

Run tests for all services:
```bash
mvn clean test
```

Run tests for a specific service:
```bash
cd user-service
mvn test
```

## Database Schema

Each service has its own set of tables following the BaseEntity pattern:
- `id` - Internal primary key
- `reference_id` - External UUID for API
- `created_at`, `updated_at` - Audit fields
- `version` - Optimistic locking

## Key Features

- JWT-based authentication
- Pessimistic locking for seat booking
- UUID-based external references
- Dockerized deployment
- Comprehensive test data loading
- RESTful APIs with consistent response format

## Development

### Building Services
```bash
mvn clean package -DskipTests
```

### Running with specific profile
```bash
java -jar target/*.jar --spring.profiles.active=docker
```
