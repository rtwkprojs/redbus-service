#!/bin/bash

echo "==================================="
echo "Loading Test Data for RedBus System"
echo "==================================="
echo ""

# Base URLs
USER_SERVICE="http://localhost:8081"
AGENCY_SERVICE="http://localhost:8082"
JOURNEY_SERVICE="http://localhost:8083"
BOOKING_SERVICE="http://localhost:8084"
PAYMENT_SERVICE="http://localhost:8086"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if service is up
check_service() {
    local url=$1
    local name=$2
    
    if curl -s -f "$url/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $name is running"
        return 0
    else
        echo -e "${RED}✗${NC} $name is not running"
        return 1
    fi
}

# Check all services
echo "Checking services..."
check_service "$USER_SERVICE" "User Service" || exit 1
check_service "$AGENCY_SERVICE" "Agency Service" || exit 1
check_service "$JOURNEY_SERVICE" "Journey Service" || exit 1
check_service "$BOOKING_SERVICE" "Booking Service" || exit 1
check_service "$PAYMENT_SERVICE" "Payment Service" || exit 1
echo ""

# Create Users
echo "Creating users..."
USER1_RESPONSE=$(curl -s -X POST "$USER_SERVICE/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "9876543210"
  }')

USER2_RESPONSE=$(curl -s -X POST "$USER_SERVICE/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane.smith",
    "email": "jane@example.com",
    "password": "password123",
    "fullName": "Jane Smith",
    "phoneNumber": "9876543211"
  }')

echo -e "${GREEN}✓${NC} Users created"

# Login to get tokens
echo "Logging in users..."
LOGIN1=$(curl -s -X POST "$USER_SERVICE/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "password123"
  }')
TOKEN1=$(echo $LOGIN1 | jq -r '.data.token // empty')

echo -e "${GREEN}✓${NC} Users logged in"
echo ""

# Create Agencies
echo "Creating agencies..."
AGENCY1=$(curl -s -X POST "$AGENCY_SERVICE/api/v1/agencies" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN1" \
  -d '{
    "agencyName": "RedBus Travels",
    "contactEmail": "contact@redbus.com",
    "contactPhone": "1234567890",
    "address": "123 Main Street, Bangalore"
  }')
AGENCY1_ID=$(echo $AGENCY1 | jq -r '.data.referenceId // empty')

AGENCY2=$(curl -s -X POST "$AGENCY_SERVICE/api/v1/agencies" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN1" \
  -d '{
    "agencyName": "BlueLine Express",
    "contactEmail": "contact@blueline.com",
    "contactPhone": "1234567891",
    "address": "456 Park Avenue, Mumbai"
  }')
AGENCY2_ID=$(echo $AGENCY2 | jq -r '.data.referenceId // empty')

echo -e "${GREEN}✓${NC} Agencies created"

# Create Vehicles
echo "Creating vehicles..."
VEHICLE1=$(curl -s -X POST "$AGENCY_SERVICE/api/v1/agencies/$AGENCY1_ID/vehicles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN1" \
  -d '{
    "registrationNumber": "KA01AB1234",
    "vehicleType": "VOLVO",
    "totalSeats": 40,
    "manufacturer": "Volvo",
    "model": "9400",
    "yearOfManufacture": 2022,
    "hasAC": true,
    "hasWifi": true,
    "hasChargingPoints": true
  }')
VEHICLE1_ID=$(echo $VEHICLE1 | jq -r '.data.referenceId // empty')

VEHICLE2=$(curl -s -X POST "$AGENCY_SERVICE/api/v1/agencies/$AGENCY2_ID/vehicles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN1" \
  -d '{
    "registrationNumber": "MH02CD5678",
    "vehicleType": "SLEEPER",
    "totalSeats": 36,
    "manufacturer": "Mercedes",
    "model": "Benz",
    "yearOfManufacture": 2023,
    "hasAC": true,
    "hasWifi": false,
    "hasChargingPoints": true
  }')
VEHICLE2_ID=$(echo $VEHICLE2 | jq -r '.data.referenceId // empty')

echo -e "${GREEN}✓${NC} Vehicles created"

# Create Stops
echo "Creating stops..."
STOPS=(
  '{"stopName": "Bangalore Central", "city": "Bangalore", "state": "Karnataka", "address": "Majestic Bus Stand", "latitude": 12.9716, "longitude": 77.5946}'
  '{"stopName": "Electronic City", "city": "Bangalore", "state": "Karnataka", "address": "Electronic City Bus Stop", "latitude": 12.8456, "longitude": 77.6603}'
  '{"stopName": "Chennai Central", "city": "Chennai", "state": "Tamil Nadu", "address": "CMBT Bus Stand", "latitude": 13.0827, "longitude": 80.2707}'
  '{"stopName": "Mumbai Central", "city": "Mumbai", "state": "Maharashtra", "address": "Mumbai Central Bus Stand", "latitude": 19.0760, "longitude": 72.8777}'
  '{"stopName": "Pune Station", "city": "Pune", "state": "Maharashtra", "address": "Pune Bus Stand", "latitude": 18.5204, "longitude": 73.8567}'
)

STOP_IDS=()
for stop in "${STOPS[@]}"; do
  RESPONSE=$(curl -s -X POST "$JOURNEY_SERVICE/api/v1/stops" \
    -H "Content-Type: application/json" \
    -d "$stop")
  STOP_ID=$(echo $RESPONSE | jq -r '.data.referenceId // empty')
  STOP_IDS+=($STOP_ID)
done

echo -e "${GREEN}✓${NC} Stops created"

# Create Routes
echo "Creating routes..."
ROUTE1=$(curl -s -X POST "$JOURNEY_SERVICE/api/v1/routes" \
  -H "Content-Type: application/json" \
  -d "{
    \"routeName\": \"Bangalore to Chennai Express\",
    \"sourceCity\": \"Bangalore\",
    \"destinationCity\": \"Chennai\",
    \"distanceKm\": 350,
    \"estimatedDurationMinutes\": 360,
    \"baseFare\": 800.0,
    \"agencyReferenceId\": \"$AGENCY1_ID\",
    \"isActive\": true,
    \"stops\": [
      {
        \"stopReferenceId\": \"${STOP_IDS[0]}\",
        \"stopSequence\": 1,
        \"arrivalTimeOffsetMinutes\": 0,
        \"departureTimeOffsetMinutes\": 0,
        \"fareFromOrigin\": 0.0,
        \"isBoardingPoint\": true,
        \"isDroppingPoint\": false
      },
      {
        \"stopReferenceId\": \"${STOP_IDS[1]}\",
        \"stopSequence\": 2,
        \"arrivalTimeOffsetMinutes\": 60,
        \"departureTimeOffsetMinutes\": 70,
        \"fareFromOrigin\": 200.0,
        \"isBoardingPoint\": true,
        \"isDroppingPoint\": false
      },
      {
        \"stopReferenceId\": \"${STOP_IDS[2]}\",
        \"stopSequence\": 3,
        \"arrivalTimeOffsetMinutes\": 360,
        \"departureTimeOffsetMinutes\": 360,
        \"fareFromOrigin\": 800.0,
        \"isBoardingPoint\": false,
        \"isDroppingPoint\": true
      }
    ]
  }")
ROUTE1_ID=$(echo $ROUTE1 | jq -r '.data.referenceId // empty')

ROUTE2=$(curl -s -X POST "$JOURNEY_SERVICE/api/v1/routes" \
  -H "Content-Type: application/json" \
  -d "{
    \"routeName\": \"Mumbai to Pune Express\",
    \"sourceCity\": \"Mumbai\",
    \"destinationCity\": \"Pune\",
    \"distanceKm\": 150,
    \"estimatedDurationMinutes\": 180,
    \"baseFare\": 400.0,
    \"agencyReferenceId\": \"$AGENCY2_ID\",
    \"isActive\": true,
    \"stops\": [
      {
        \"stopReferenceId\": \"${STOP_IDS[3]}\",
        \"stopSequence\": 1,
        \"arrivalTimeOffsetMinutes\": 0,
        \"departureTimeOffsetMinutes\": 0,
        \"fareFromOrigin\": 0.0,
        \"isBoardingPoint\": true,
        \"isDroppingPoint\": false
      },
      {
        \"stopReferenceId\": \"${STOP_IDS[4]}\",
        \"stopSequence\": 2,
        \"arrivalTimeOffsetMinutes\": 180,
        \"departureTimeOffsetMinutes\": 180,
        \"fareFromOrigin\": 400.0,
        \"isBoardingPoint\": false,
        \"isDroppingPoint\": true
      }
    ]
  }")
ROUTE2_ID=$(echo $ROUTE2 | jq -r '.data.referenceId // empty')

echo -e "${GREEN}✓${NC} Routes created"

# Create Journeys
echo "Creating journeys..."
TOMORROW=$(date -v+1d +%Y-%m-%d 2>/dev/null || date -d tomorrow +%Y-%m-%d)
DAY_AFTER=$(date -v+2d +%Y-%m-%d 2>/dev/null || date -d "+2 days" +%Y-%m-%d)

JOURNEY1=$(curl -s -X POST "$JOURNEY_SERVICE/api/v1/journeys" \
  -H "Content-Type: application/json" \
  -d "{
    \"routeReferenceId\": \"$ROUTE1_ID\",
    \"vehicleReferenceId\": \"$VEHICLE1_ID\",
    \"departureTime\": \"${TOMORROW}T08:00:00\",
    \"arrivalTime\": \"${TOMORROW}T14:00:00\",
    \"baseFare\": 800.0,
    \"totalSeats\": 40,
    \"availableSeats\": 40,
    \"amenities\": [\"AC\", \"WIFI\", \"WATER_BOTTLE\"],
    \"isActive\": true
  }")
JOURNEY1_ID=$(echo $JOURNEY1 | jq -r '.data.referenceId // empty')

JOURNEY2=$(curl -s -X POST "$JOURNEY_SERVICE/api/v1/journeys" \
  -H "Content-Type: application/json" \
  -d "{
    \"routeReferenceId\": \"$ROUTE1_ID\",
    \"vehicleReferenceId\": \"$VEHICLE1_ID\",
    \"departureTime\": \"${TOMORROW}T20:00:00\",
    \"arrivalTime\": \"${DAY_AFTER}T02:00:00\",
    \"baseFare\": 900.0,
    \"totalSeats\": 40,
    \"availableSeats\": 40,
    \"amenities\": [\"AC\", \"WIFI\", \"BLANKET\"],
    \"isActive\": true
  }")
JOURNEY2_ID=$(echo $JOURNEY2 | jq -r '.data.referenceId // empty')

JOURNEY3=$(curl -s -X POST "$JOURNEY_SERVICE/api/v1/journeys" \
  -H "Content-Type: application/json" \
  -d "{
    \"routeReferenceId\": \"$ROUTE2_ID\",
    \"vehicleReferenceId\": \"$VEHICLE2_ID\",
    \"departureTime\": \"${TOMORROW}T10:00:00\",
    \"arrivalTime\": \"${TOMORROW}T13:00:00\",
    \"baseFare\": 400.0,
    \"totalSeats\": 36,
    \"availableSeats\": 36,
    \"amenities\": [\"AC\", \"CHARGING_PORT\"],
    \"isActive\": true
  }")
JOURNEY3_ID=$(echo $JOURNEY3 | jq -r '.data.referenceId // empty')

echo -e "${GREEN}✓${NC} Journeys created"
echo ""

# Summary
echo "==================================="
echo "Test Data Loaded Successfully!"
echo "==================================="
echo ""
echo "Created:"
echo "  - 2 Users"
echo "  - 2 Agencies"
echo "  - 2 Vehicles"
echo "  - 5 Stops"
echo "  - 2 Routes"
echo "  - 3 Journeys (for tomorrow)"
echo ""
echo "Sample Journey IDs for testing:"
echo "  Bangalore to Chennai (Morning): $JOURNEY1_ID"
echo "  Bangalore to Chennai (Night): $JOURNEY2_ID"
echo "  Mumbai to Pune: $JOURNEY3_ID"
echo ""
echo "You can now test booking flows with these journey IDs!"
