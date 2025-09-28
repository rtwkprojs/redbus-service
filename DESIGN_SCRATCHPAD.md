# Bus Booking System - Design Document

## Executive Summary
Production-ready POC for a bus booking platform using Spring Boot 3.4.10 with Java 17, implementing microservices architecture with multi-module Maven structure. The system enables bus agencies to manage their fleet and routes while customers can search, book, and manage tickets.

## System Architecture

### Technology Stack
- **Framework**: Spring Boot 3.4.10
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Search Engine**: Elasticsearch 8.x
- **Message Broker**: Apache Kafka
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build Tool**: Maven (Multi-module)

### Module Structure
```
bus-booking-system/
├── pom.xml                    # Parent POM
├── common/                    # Shared components
│   ├── dto/                  # Data Transfer Objects
│   ├── entity/               # JPA Base Entities
│   ├── exception/            # Custom Exceptions
│   └── util/                 # Utility Classes
├── user-service/             # User Management
├── agency-service/           # Agency & Vehicle Management
├── journey-service/          # Routes & Journey Management
├── search-service/           # Elasticsearch Integration
├── booking-service/          # Booking Orchestration
└── payment-service/          # Payment Processing (Mock)
```

## Database Design

### Base Entity Pattern
All tables will extend from a base entity with common fields:

```java
@MappedSuperclass
@Data
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Internal ID for database operations
    
    @Column(name = "reference_id", unique = true, nullable = false)
    private UUID referenceId = UUID.randomUUID();  // External ID for API exposure
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;  // Optimistic locking
}
```

### Core Tables

#### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(20) CHECK (user_type IN ('CUSTOMER', 'AGENCY_ADMIN')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### agencies
```sql
CREATE TABLE agencies (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address TEXT,
    created_by_id BIGINT REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### vehicles
```sql
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    agency_id BIGINT REFERENCES agencies(id),
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    capacity INTEGER NOT NULL,
    vehicle_type VARCHAR(50), -- AC_SLEEPER, NON_AC_SEATER, etc.
    amenities JSONB,
    seat_layout JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### routes
```sql
CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    agency_id BIGINT REFERENCES agencies(id),
    route_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### stops
```sql
CREATE TABLE stops (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    stop_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### route_stops
```sql
CREATE TABLE route_stops (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_id BIGINT REFERENCES routes(id),
    stop_id BIGINT REFERENCES stops(id),
    stop_sequence INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE(route_id, stop_id)
);
```

#### journeys
```sql
CREATE TABLE journeys (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_id BIGINT REFERENCES routes(id),
    vehicle_id BIGINT REFERENCES vehicles(id),
    source_stop_id BIGINT REFERENCES stops(id),
    destination_stop_id BIGINT REFERENCES stops(id),
    departure_time TIMESTAMPTZ NOT NULL,
    arrival_time TIMESTAMPTZ NOT NULL,
    base_fare DECIMAL(10, 2),
    available_seats INTEGER,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### bookings
```sql
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    customer_id BIGINT REFERENCES users(id),
    journey_id BIGINT REFERENCES journeys(id),
    booking_status VARCHAR(20) DEFAULT 'PENDING',
    total_fare DECIMAL(10, 2),
    lock_expiry TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### tickets
```sql
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    booking_id BIGINT REFERENCES bookings(id),
    pnr VARCHAR(10) UNIQUE NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_age INTEGER,
    seat_number VARCHAR(10) NOT NULL,
    fare DECIMAL(10, 2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

#### payments
```sql
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    booking_id BIGINT REFERENCES bookings(id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);
```

### Database Indexes
```sql
-- Performance indexes for queries
CREATE INDEX idx_journeys_departure ON journeys(departure_time);
CREATE INDEX idx_journeys_route ON journeys(route_id);
CREATE INDEX idx_journeys_source_dest ON journeys(source_stop_id, destination_stop_id);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_journey ON bookings(journey_id);
CREATE INDEX idx_tickets_pnr ON tickets(pnr);

-- Reference ID indexes for API lookups
CREATE INDEX idx_users_reference_id ON users(reference_id);
CREATE INDEX idx_agencies_reference_id ON agencies(reference_id);
CREATE INDEX idx_vehicles_reference_id ON vehicles(reference_id);
CREATE INDEX idx_routes_reference_id ON routes(reference_id);
CREATE INDEX idx_stops_reference_id ON stops(reference_id);
CREATE INDEX idx_journeys_reference_id ON journeys(reference_id);
CREATE INDEX idx_bookings_reference_id ON bookings(reference_id);
CREATE INDEX idx_tickets_reference_id ON tickets(reference_id);
CREATE INDEX idx_payments_reference_id ON payments(reference_id);
```

## Service Architecture

### 1. User Service
**Responsibilities:**
- User registration and authentication
- Password management with BCrypt
- User profile management
- Role-based access control

**Key APIs:**
- POST /api/users/register
- POST /api/users/login
- GET /api/users/profile
- PUT /api/users/profile

### 2. Agency Service
**Responsibilities:**
- Agency CRUD operations
- Vehicle management
- Agency admin operations

**Key APIs:**
- POST /api/agencies
- GET /api/agencies/{id}
- POST /api/agencies/{id}/vehicles
- PUT /api/agencies/{id}/vehicles/{vehicleId}

### 3. Journey Service
**Responsibilities:**
- Route management
- Stop management
- Journey scheduling
- Automatic journey generation from routes

**Key APIs:**
- POST /api/routes
- POST /api/routes/{id}/stops
- POST /api/journeys
- GET /api/journeys/{id}

### 4. Search Service
**Responsibilities:**
- Elasticsearch integration
- Journey search and filtering
- Real-time availability checks

**Key APIs:**
- GET /api/search/journeys
- GET /api/search/suggestions

**Elasticsearch Schema:**
```json
{
  "mappings": {
    "properties": {
      "journey_id": { "type": "keyword" },
      "agency_name": { "type": "text" },
      "vehicle_type": { "type": "keyword" },
      "source_city": { "type": "keyword" },
      "destination_city": { "type": "keyword" },
      "departure_datetime": { "type": "date" },
      "arrival_datetime": { "type": "date" },
      "duration_minutes": { "type": "integer" },
      "base_fare": { "type": "float" },
      "available_seats": { "type": "integer" },
      "amenities": { "type": "keyword" }
    }
  }
}
```

### 5. Booking Service
**Responsibilities:**
- Booking orchestration
- Seat locking with database transactions and pessimistic locking
- Booking state management
- PNR generation

**Key APIs:**
- POST /api/bookings/initiate
- POST /api/bookings/{id}/confirm
- GET /api/bookings/{id}
- POST /api/bookings/{id}/cancel

**Distributed Locking Strategy:**
```java
// Use SERIALIZABLE isolation level with pessimistic locking
@Transactional(isolation = Isolation.SERIALIZABLE)
public BookingResponse initiateBooking(BookingRequest request) {
    // Acquire pessimistic write lock on journey
    Journey journey = journeyRepository.findByIdForUpdate(request.getJourneyId())
        .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
    
    // Check and update available seats
    if (journey.getAvailableSeats() < request.getSeats().size()) {
        throw new InsufficientSeatsException("Not enough seats available");
    }
    
    // Create booking with PENDING status
    Booking booking = new Booking();
    booking.setStatus(BookingStatus.PENDING);
    booking.setLockExpiry(LocalDateTime.now().plusMinutes(10));
    
    // Decrease available seats
    journey.setAvailableSeats(journey.getAvailableSeats() - request.getSeats().size());
    
    return bookingRepository.save(booking);
}

// Repository method with pessimistic lock
@Query("SELECT j FROM Journey j WHERE j.id = :id")
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Journey> findByIdForUpdate(@Param("id") Long id);
```

### 6. Payment Service
**Responsibilities:**
- Mock payment processing
- Payment status management
- Refund processing

**Key APIs:**
- POST /api/payments/process
- GET /api/payments/{id}
- POST /api/payments/{id}/refund

## Event-Driven Architecture

### Kafka Topics
1. **journey-events**: Journey creation/updates
2. **booking-events**: Booking state changes
3. **payment-events**: Payment confirmations

### Event Flow
```
Journey Created → journey-events → Search Service → Index to ES
Booking Confirmed → booking-events → Notification Service
Payment Success → payment-events → Booking Service → Confirm Booking
```

## Design Patterns Implementation

### 1. Repository Pattern
```java
@Repository
public interface JourneyRepository extends JpaRepository<Journey, UUID> {
    List<Journey> findByRouteIdAndDepartureTimeBetween(
        UUID routeId, LocalDateTime start, LocalDateTime end);
}
```

### 2. Service Layer Pattern
```java
@Service
@Transactional
public class BookingService {
    public BookingResponse initiateBooking(BookingRequest request) {
        // Business logic
    }
}
```

### 3. DTO Pattern
```java
@Data
@Builder
public class JourneySearchDto {
    private UUID journeyId;
    private String sourceCity;
    private String destinationCity;
    private LocalDateTime departureTime;
}
```

### 4. Factory Pattern
```java
public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request);
}

@Component
public class PaymentProcessorFactory {
    public PaymentProcessor getProcessor(PaymentMethod method) {
        // Return appropriate processor
    }
}
```

## Testing Strategy

### Unit Testing
- Service layer with Mockito
- Repository layer with @DataJpaTest
- Controller layer with @WebMvcTest
- Target: 80% code coverage

### Integration Testing
- Full flow testing with TestContainers
- API contract testing
- Database transaction testing

### Test Structure
```java
@SpringBootTest
@AutoConfigureMockMvc
@TestContainers
class BookingFlowIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static KafkaContainer kafka = 
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    
    @Test
    void shouldCompleteBookingFlow() {
        // Test implementation
    }
}
```

## Security Considerations

1. **Authentication**: Simple JWT-based auth
2. **Password Storage**: BCrypt with salt
3. **Input Validation**: Bean Validation API
4. **SQL Injection Prevention**: JPA/Prepared Statements
5. **CORS Configuration**: Configurable per environment

## Performance Optimizations

1. **Database Connection Pooling**: HikariCP
2. **Caching**: Spring Cache with Caffeine
3. **Batch Processing**: Journey generation
4. **Async Processing**: @Async for notifications
5. **Database Indexing**: Strategic indexes

## Deployment Strategy

### Docker Compose Configuration
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: redbus
      POSTGRES_USER: redbus
      POSTGRES_PASSWORD: redbus123
    ports:
      - "5432:5432"
  
  elasticsearch:
    image: elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
  
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

## Configuration Management

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/redbus
    username: redbus
    password: redbus123
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  
  elasticsearch:
    uris: http://localhost:9200
```

## API Documentation
- SpringDoc OpenAPI for automatic API documentation
- Accessible at: http://localhost:8080/swagger-ui.html

## Monitoring & Logging
- Spring Boot Actuator for health checks
- SLF4J with Logback for logging
- Structured logging with JSON format

## Known Trade-offs & Simplifications

1. **Single Database**: All services share one PostgreSQL (different schemas)
2. **No API Gateway**: Direct service access for POC
3. **Mock Payment**: Simple amount comparison instead of real gateway
4. **Synchronous Calls**: Some inter-service communication is synchronous
5. **No Circuit Breaker**: Simplified error handling

## Future Enhancements

1. Implement Redis for caching and session management
2. Add real payment gateway integration
3. Implement API Gateway with rate limiting
4. Add comprehensive monitoring with Prometheus/Grafana
5. Implement circuit breaker pattern with Resilience4j
6. Add GraphQL API support
7. Implement WebSocket for real-time seat availability
