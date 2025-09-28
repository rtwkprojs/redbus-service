# Bus Booking System - API Documentation

## User Service (Port 8081)

### Base URL
```
http://localhost:8081/api/v1/users
```

### Authentication
JWT tokens are used for authentication. Include the token in the `Authorization` header:
```
Authorization: Bearer <token>
```

### Endpoints

#### 1. Register User
**POST** `/register`

Register a new user account.

**Request Body:**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test@123",
  "phone": "9876543210",
  "userType": "CUSTOMER"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "referenceId": "uuid",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "9876543210",
    "userType": "CUSTOMER",
    "createdAt": "2025-09-28T10:45:30.950712",
    "updatedAt": "2025-09-28T10:45:30.950737"
  },
  "timestamp": "2025-09-28T10:45:30.96968"
}
```

#### 2. User Login
**POST** `/login`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "usernameOrEmail": "testuser",
  "password": "Test@123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "referenceId": "uuid",
      "username": "testuser",
      "email": "test@example.com",
      "phone": "9876543210",
      "userType": "CUSTOMER",
      "createdAt": "2025-09-28T10:45:30.950712",
      "updatedAt": "2025-09-28T10:45:30.950737"
    }
  },
  "timestamp": "2025-09-28T10:47:01.78196"
}
```

#### 3. Get User by Reference ID
**GET** `/{referenceId}`

Retrieve user details by their reference ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "referenceId": "uuid",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "9876543210",
    "userType": "CUSTOMER",
    "createdAt": "2025-09-28T10:45:30.950712",
    "updatedAt": "2025-09-28T10:45:30.950737"
  },
  "timestamp": "2025-09-28T10:47:01.78196"
}
```

#### 4. Get User by Username
**GET** `/username/{username}`

Retrieve user details by username.

#### 5. Update User
**PUT** `/{referenceId}`

Update user profile information.

**Request Body:**
```json
{
  "email": "newemail@example.com",
  "phone": "9876543211"
}
```

#### 6. Change Password
**POST** `/{referenceId}/change-password`

Change user password.

**Request Body:**
```json
{
  "currentPassword": "OldPass@123",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```

#### 7. Delete User
**DELETE** `/{referenceId}`

Delete a user account.

#### 8. Check Username Availability
**GET** `/check-username/{username}`

Check if a username is available.

**Response:**
```json
{
  "success": true,
  "data": false
}
```

#### 9. Check Email Availability
**GET** `/check-email/{email}`

Check if an email is already registered.

**Response:**
```json
{
  "success": true,
  "data": false
}
```

## Error Responses

All error responses follow this format:

```json
{
  "success": false,
  "message": "Error message",
  "error": {
    "code": "ERROR_CODE",
    "message": "Detailed error message",
    "details": "Additional details",
    "path": "/api/v1/users/endpoint",
    "timestamp": "2025-09-28T10:45:44.590309",
    "validationErrors": null
  },
  "timestamp": "2025-09-28T10:45:44.590321"
}
```

### Common Error Codes
- `VALIDATION_ERROR` - Input validation failed
- `RESOURCE_NOT_FOUND` - Requested resource not found
- `BUSINESS_ERROR` - Business logic error (e.g., invalid credentials)
- `INTERNAL_ERROR` - Server error

## Validation Rules

### Username
- Required
- 3-50 characters
- Alphanumeric and underscores only

### Email
- Required
- Valid email format
- Max 100 characters

### Password
- Required
- 6-100 characters
- Must contain at least one uppercase letter, one lowercase letter, and one number

### Phone
- Optional
- Must be exactly 10 digits

### UserType
- Required
- Must be either `CUSTOMER` or `AGENCY_ADMIN`

## Testing with cURL

### Register
```bash
curl -X POST http://localhost:8081/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@123",
    "phone": "9876543210",
    "userType": "CUSTOMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "Test@123"
  }'
```

### Authenticated Request
```bash
curl -X GET http://localhost:8081/api/v1/users/{referenceId} \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Agency Service (Port 8082)

### Base URL
```
http://localhost:8082/api/v1/agencies
```

### Agency Endpoints

#### 1. Create Agency
**POST** `/`

Create a new bus agency.

**Request Body:**
```json
{
  "agencyName": "SpeedBus Travels",
  "contactEmail": "contact@speedbus.com",
  "contactPhone": "9876543210",
  "address": "123 Main Street, Mumbai"
}
```

#### 2. Update Agency
**PUT** `/{referenceId}`

#### 3. Get Agency
**GET** `/{referenceId}`

#### 4. List All Active Agencies
**GET** `/`

#### 5. Get My Agencies
**GET** `/my-agencies`

#### 6. Delete Agency
**DELETE** `/{referenceId}`

#### 7. Activate/Deactivate Agency
**POST** `/{referenceId}/activate`
**POST** `/{referenceId}/deactivate`

### Vehicle Endpoints

#### 1. Add Vehicle to Agency
**POST** `/{agencyReferenceId}/vehicles`

**Request Body:**
```json
{
  "registrationNumber": "MH01AB1234",
  "vehicleType": "VOLVO",
  "totalSeats": 40,
  "manufacturer": "Volvo",
  "model": "9400 Multi-Axle",
  "yearOfManufacture": 2022,
  "hasAC": true,
  "hasWifi": true,
  "hasChargingPoints": true
}
```

#### 2. Update Vehicle
**PUT** `/{agencyReferenceId}/vehicles/{vehicleReferenceId}`

#### 3. List Agency Vehicles
**GET** `/{agencyReferenceId}/vehicles`

#### 4. Get Vehicle
**GET** `/vehicles/{vehicleReferenceId}`

#### 5. Delete Vehicle
**DELETE** `/{agencyReferenceId}/vehicles/{vehicleReferenceId}`

#### 6. Activate/Deactivate Vehicle
**POST** `/{agencyReferenceId}/vehicles/{vehicleReferenceId}/activate`
**POST** `/{agencyReferenceId}/vehicles/{vehicleReferenceId}/deactivate`

### Vehicle Types
- `SEATER` - Regular seater bus
- `SLEEPER` - Sleeper bus
- `SEMI_SLEEPER` - Semi-sleeper bus
- `VOLVO` - Volvo bus
- `AC_SEATER` - AC seater bus
- `NON_AC_SEATER` - Non-AC seater bus
- `DELUXE` - Deluxe bus
- `LUXURY` - Luxury bus

## Next Services (To Be Implemented)
- **Journey Service** (Port 8083) - Routes, stops, and journey scheduling
- **Search Service** (Port 8084) - Elasticsearch-based journey search
- **Booking Service** (Port 8085) - Booking orchestration with seat locking
- **Payment Service** (Port 8086) - Mock payment processing
