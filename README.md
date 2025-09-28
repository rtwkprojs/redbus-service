# Bus Booking System

A production-ready POC for a bus booking platform built with Spring Boot 3.4.10, Java 17, PostgreSQL, Elasticsearch, and Kafka using a multi-module Maven architecture.

## Architecture

The system follows a microservices architecture with the following modules:

- **common**: Shared components, DTOs, exceptions, and utilities
- **user-service**: User registration, authentication, and profile management
- **agency-service**: Bus agency and vehicle management
- **journey-service**: Routes, stops, and journey scheduling
- **search-service**: Elasticsearch-based journey search
- **booking-service**: Booking orchestration with seat locking
- **payment-service**: Mock payment processing

## Technology Stack

- **Framework**: Spring Boot 3.4.10
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Search**: Elasticsearch 8.11
- **Message Broker**: Apache Kafka
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito, TestContainers

## Prerequisites

- Java 17
- Maven 3.8+
- Docker & Docker Compose
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd core
```

### 2. Start Infrastructure Services

Start PostgreSQL, Elasticsearch, Kafka, and Zookeeper using Docker Compose:

```bash
docker-compose up -d
```

Wait for all services to be healthy:

```bash
docker-compose ps
```

### 3. Build the Project

Build all modules:

```bash
mvn clean install
```

### 4. Run Database Migrations

The database schema and initial data are automatically created using Flyway migrations when you start the user-service.

### 5. Run Services

Start each service in separate terminals:

#### User Service (Port 8081)
```bash
cd user-service
mvn spring-boot:run
```

#### Agency Service (Port 8082)
```bash
cd agency-service
mvn spring-boot:run
```

#### Journey Service (Port 8083)
```bash
cd journey-service
mvn spring-boot:run
```

#### Search Service (Port 8084)
```bash
cd search-service
mvn spring-boot:run
```

#### Booking Service (Port 8085)
```bash
cd booking-service
mvn spring-boot:run
```

#### Payment Service (Port 8086)
```bash
cd payment-service
mvn spring-boot:run
```

## API Documentation

Each service provides Swagger UI for API documentation:

- User Service: http://localhost:8081/swagger-ui.html
- Agency Service: http://localhost:8082/swagger-ui.html
- Journey Service: http://localhost:8083/swagger-ui.html
- Search Service: http://localhost:8084/swagger-ui.html
- Booking Service: http://localhost:8085/swagger-ui.html
- Payment Service: http://localhost:8086/swagger-ui.html

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

Generate test coverage report:

```bash
mvn jacoco:report
```

## Key Features

### User Management
- User registration with email and username validation
- JWT-based authentication
- Password hashing with BCrypt
- Profile management

### Journey Management
- Route definition with multiple stops
- Automatic journey generation from routes
- Vehicle assignment and capacity management
- Real-time seat availability

### Booking System
- SERIALIZABLE isolation level with pessimistic locking
- Prevents overselling of seats
- PNR generation for tickets
- Booking status management

### Search
- Elasticsearch-based journey search
- Filter by date, source, destination
- Real-time indexing via Kafka events

## Database Design

All tables extend BaseEntity with:
- `id`: Internal database primary key (Long)
- `reference_id`: External API identifier (UUID)
- `created_at`: Audit timestamp
- `updated_at`: Last modification timestamp
- `version`: Optimistic locking version

## Environment Variables

Key environment variables (see `.env` file):

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=redbus
DB_USERNAME=redbus
DB_PASSWORD=redbus123

ES_HOST=localhost
ES_PORT=9200

KAFKA_BOOTSTRAP_SERVERS=localhost:9092

JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000
```

## Monitoring

### Health Checks

Each service exposes health endpoints:

```bash
curl http://localhost:8081/actuator/health
```

### Kafka UI

Monitor Kafka topics and messages:

http://localhost:8090

### Logs

Logs are written to:
- Console (for development)
- `logs/<service-name>.log` (for production)

## Development

### Adding a New Service

1. Create a new module directory
2. Add module to parent `pom.xml`
3. Create module `pom.xml` with dependencies
4. Implement service following the existing pattern
5. Add configuration in `application.yml`
6. Create tests

### Code Style

- Follow Spring Boot best practices
- Use Lombok for boilerplate reduction
- Implement proper error handling
- Write comprehensive tests (target 80% coverage)
- Use meaningful commit messages

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   - Check if services are already running
   - Change ports in application.yml

2. **Database Connection Failed**
   - Ensure PostgreSQL is running: `docker-compose ps`
   - Check credentials in application.yml

3. **Elasticsearch Not Responding**
   - Check ES health: `curl http://localhost:9200/_cluster/health`
   - Increase memory if needed in docker-compose.yml

4. **Kafka Connection Issues**
   - Ensure Zookeeper is running before Kafka
   - Check Kafka logs: `docker-compose logs kafka`

## Production Deployment

For production deployment:

1. Use external database with connection pooling
2. Deploy Elasticsearch cluster
3. Set up Kafka cluster with multiple brokers
4. Use proper secrets management (e.g., HashiCorp Vault)
5. Implement API Gateway (e.g., Spring Cloud Gateway)
6. Add monitoring (Prometheus + Grafana)
7. Implement circuit breakers (Resilience4j)

## License

This is a POC project for educational purposes.

## Contact

For questions or issues, please create a GitHub issue.
