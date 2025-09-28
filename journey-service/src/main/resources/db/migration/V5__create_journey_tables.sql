-- Create stops table
CREATE TABLE IF NOT EXISTS stops (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    stop_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    landmark VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_stop_location UNIQUE(stop_name, city, state)
);

-- Create routes table
CREATE TABLE IF NOT EXISTS routes (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_name VARCHAR(200) NOT NULL,
    source_city VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    distance_km INTEGER NOT NULL,
    estimated_duration_minutes INTEGER NOT NULL,
    base_fare DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    agency_reference_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create route_stops junction table
CREATE TABLE IF NOT EXISTS route_stops (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    stop_sequence INTEGER NOT NULL,
    arrival_time_offset_minutes INTEGER NOT NULL,
    departure_time_offset_minutes INTEGER NOT NULL,
    distance_from_previous_km INTEGER,
    fare_from_origin DECIMAL(10,2),
    is_boarding_point BOOLEAN NOT NULL DEFAULT true,
    is_dropping_point BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_route_stop_route FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_stop_stop FOREIGN KEY (stop_id) REFERENCES stops(id),
    CONSTRAINT uk_route_stop_sequence UNIQUE(route_id, stop_sequence)
);

-- Create journeys table
CREATE TABLE IF NOT EXISTS journeys (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_id BIGINT NOT NULL,
    journey_code VARCHAR(20) UNIQUE NOT NULL,
    vehicle_reference_id VARCHAR(100) NOT NULL,
    departure_time TIMESTAMPTZ NOT NULL,
    arrival_time TIMESTAMPTZ NOT NULL,
    journey_status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    base_fare DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    amenities VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_journey_route FOREIGN KEY (route_id) REFERENCES routes(id)
);

-- Create seat_inventory table
CREATE TABLE IF NOT EXISTS seat_inventory (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    journey_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    seat_type VARCHAR(20) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT true,
    is_ladies_seat BOOLEAN NOT NULL DEFAULT false,
    fare_multiplier DECIMAL(3,2) NOT NULL DEFAULT 1.0,
    booking_reference_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_seat_journey FOREIGN KEY (journey_id) REFERENCES journeys(id) ON DELETE CASCADE,
    CONSTRAINT uk_journey_seat UNIQUE(journey_id, seat_number)
);

-- Create indexes
CREATE INDEX idx_stops_city ON stops(city);
CREATE INDEX idx_stops_state ON stops(state);
CREATE INDEX idx_stops_is_active ON stops(is_active);

CREATE INDEX idx_routes_reference_id ON routes(reference_id);
CREATE INDEX idx_routes_agency_reference_id ON routes(agency_reference_id);
CREATE INDEX idx_routes_source_city ON routes(source_city);
CREATE INDEX idx_routes_destination_city ON routes(destination_city);
CREATE INDEX idx_routes_is_active ON routes(is_active);

CREATE INDEX idx_route_stops_route_id ON route_stops(route_id);
CREATE INDEX idx_route_stops_stop_id ON route_stops(stop_id);

CREATE INDEX idx_journeys_reference_id ON journeys(reference_id);
CREATE INDEX idx_journeys_route_id ON journeys(route_id);
CREATE INDEX idx_journeys_journey_code ON journeys(journey_code);
CREATE INDEX idx_journeys_vehicle_reference_id ON journeys(vehicle_reference_id);
CREATE INDEX idx_journeys_departure_time ON journeys(departure_time);
CREATE INDEX idx_journeys_journey_status ON journeys(journey_status);
CREATE INDEX idx_journeys_is_active ON journeys(is_active);

CREATE INDEX idx_seat_inventory_journey_id ON seat_inventory(journey_id);
CREATE INDEX idx_seat_inventory_is_available ON seat_inventory(is_available);
CREATE INDEX idx_seat_inventory_booking_reference_id ON seat_inventory(booking_reference_id);
