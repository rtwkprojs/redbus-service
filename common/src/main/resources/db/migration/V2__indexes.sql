-- V2__indexes.sql
-- Performance and reference_id indexes for Bus Booking System

-- Performance indexes for queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_type ON users(user_type);

CREATE INDEX idx_agencies_name ON agencies(name);
CREATE INDEX idx_agencies_created_by ON agencies(created_by_id);

CREATE INDEX idx_vehicles_agency ON vehicles(agency_id);
CREATE INDEX idx_vehicles_type ON vehicles(vehicle_type);
CREATE INDEX idx_vehicles_registration ON vehicles(registration_number);

CREATE INDEX idx_routes_agency ON routes(agency_id);
CREATE INDEX idx_routes_active ON routes(is_active) WHERE is_active = true;

CREATE INDEX idx_stops_city ON stops(city);
CREATE INDEX idx_stops_state ON stops(state);
CREATE INDEX idx_stops_location ON stops(latitude, longitude);

CREATE INDEX idx_route_stops_route ON route_stops(route_id);
CREATE INDEX idx_route_stops_stop ON route_stops(stop_id);
CREATE INDEX idx_route_stops_sequence ON route_stops(route_id, stop_sequence);

CREATE INDEX idx_journeys_departure ON journeys(departure_time);
CREATE INDEX idx_journeys_arrival ON journeys(arrival_time);
CREATE INDEX idx_journeys_route ON journeys(route_id);
CREATE INDEX idx_journeys_vehicle ON journeys(vehicle_id);
CREATE INDEX idx_journeys_source_dest ON journeys(source_stop_id, destination_stop_id);
CREATE INDEX idx_journeys_status ON journeys(status);
CREATE INDEX idx_journeys_available_seats ON journeys(available_seats) WHERE available_seats > 0;

CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_journey ON bookings(journey_id);
CREATE INDEX idx_bookings_status ON bookings(booking_status);
CREATE INDEX idx_bookings_created ON bookings(created_at);
CREATE INDEX idx_bookings_lock_expiry ON bookings(lock_expiry) WHERE lock_expiry IS NOT NULL;

CREATE INDEX idx_tickets_booking ON tickets(booking_id);
CREATE INDEX idx_tickets_pnr ON tickets(pnr);

CREATE INDEX idx_payments_booking ON payments(booking_id);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_payments_transaction ON payments(transaction_id);

-- Reference ID indexes for API lookups (already unique, but explicitly indexed for performance)
CREATE INDEX idx_users_reference_id ON users(reference_id);
CREATE INDEX idx_agencies_reference_id ON agencies(reference_id);
CREATE INDEX idx_vehicles_reference_id ON vehicles(reference_id);
CREATE INDEX idx_routes_reference_id ON routes(reference_id);
CREATE INDEX idx_stops_reference_id ON stops(reference_id);
CREATE INDEX idx_route_stops_reference_id ON route_stops(reference_id);
CREATE INDEX idx_journeys_reference_id ON journeys(reference_id);
CREATE INDEX idx_bookings_reference_id ON bookings(reference_id);
CREATE INDEX idx_tickets_reference_id ON tickets(reference_id);
CREATE INDEX idx_payments_reference_id ON payments(reference_id);

-- Composite indexes for common query patterns
CREATE INDEX idx_journeys_search ON journeys(departure_time, source_stop_id, destination_stop_id, status)
    WHERE status = 'SCHEDULED';

CREATE INDEX idx_bookings_active ON bookings(customer_id, booking_status, created_at)
    WHERE booking_status IN ('PENDING', 'CONFIRMED');

CREATE INDEX idx_route_stops_ordered ON route_stops(route_id, stop_sequence, stop_id);
