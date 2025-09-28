# RedBus - Bus Booking System

A microservices-based bus booking system built with Spring Boot and PostgreSQL.

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

## API Documentation

### User Service (Port 8081)
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/users/profile` - Get user profile
- `PUT /api/v1/users/profile` - Update profile

### Agency Service (Port 8082)
- `POST /api/v1/agencies` - Create agency
- `GET /api/v1/agencies/{id}` - Get agency details
- `POST /api/v1/agencies/{id}/vehicles` - Add vehicle
- `GET /api/v1/agencies/{id}/vehicles` - List vehicles

### Journey Service (Port 8083)
- `POST /api/v1/routes` - Create route
- `POST /api/v1/stops` - Create stop
- `POST /api/v1/journeys` - Schedule journey
- `GET /api/v1/journeys/search` - Search journeys
- `GET /api/v1/journeys/{id}/seats` - Get seat inventory

### Booking Service (Port 8084)
- `POST /api/v1/bookings/initiate` - Initiate booking
- `POST /api/v1/bookings/{id}/confirm` - Confirm booking
- `POST /api/v1/bookings/{id}/cancel` - Cancel booking
- `GET /api/v1/bookings/{id}` - Get booking details

### Payment Service (Port 8086)
- `POST /api/v1/payments/process` - Process payment
- `GET /api/v1/payments/{id}` - Get payment details
- `POST /api/v1/payments/{id}/refund` - Initiate refund

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

## License

MIT
