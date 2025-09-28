-- V1__initial_schema.sql
-- Initial database schema for Bus Booking System

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('CUSTOMER', 'AGENCY_ADMIN')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Agencies table
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

-- Vehicles table
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    agency_id BIGINT NOT NULL REFERENCES agencies(id),
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    vehicle_type VARCHAR(50) NOT NULL,
    amenities JSONB,
    seat_layout JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Routes table
CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    agency_id BIGINT NOT NULL REFERENCES agencies(id),
    route_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Stops table
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

-- Route_stops junction table
CREATE TABLE route_stops (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_id BIGINT NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    stop_id BIGINT NOT NULL REFERENCES stops(id),
    stop_sequence INTEGER NOT NULL CHECK (stop_sequence > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE(route_id, stop_id),
    UNIQUE(route_id, stop_sequence)
);

-- Journeys table
CREATE TABLE journeys (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    route_id BIGINT NOT NULL REFERENCES routes(id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    source_stop_id BIGINT NOT NULL REFERENCES stops(id),
    destination_stop_id BIGINT NOT NULL REFERENCES stops(id),
    departure_time TIMESTAMPTZ NOT NULL,
    arrival_time TIMESTAMPTZ NOT NULL,
    base_fare DECIMAL(10, 2) NOT NULL CHECK (base_fare >= 0),
    available_seats INTEGER NOT NULL CHECK (available_seats >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT check_departure_before_arrival CHECK (departure_time < arrival_time),
    CONSTRAINT check_different_stops CHECK (source_stop_id != destination_stop_id)
);

-- Bookings table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    customer_id BIGINT NOT NULL REFERENCES users(id),
    journey_id BIGINT NOT NULL REFERENCES journeys(id),
    booking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (booking_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')),
    total_fare DECIMAL(10, 2) NOT NULL CHECK (total_fare >= 0),
    lock_expiry TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Tickets table
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    pnr VARCHAR(10) UNIQUE NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_age INTEGER CHECK (passenger_age > 0 AND passenger_age < 150),
    seat_number VARCHAR(10) NOT NULL,
    fare DECIMAL(10, 2) NOT NULL CHECK (fare >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    payment_status VARCHAR(20) NOT NULL CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Create update trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add update triggers to all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_agencies_updated_at BEFORE UPDATE ON agencies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_vehicles_updated_at BEFORE UPDATE ON vehicles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_routes_updated_at BEFORE UPDATE ON routes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_stops_updated_at BEFORE UPDATE ON stops
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_route_stops_updated_at BEFORE UPDATE ON route_stops
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_journeys_updated_at BEFORE UPDATE ON journeys
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bookings_updated_at BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tickets_updated_at BEFORE UPDATE ON tickets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
