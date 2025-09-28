-- V3__seed_data.sql
-- Seed data for testing the Bus Booking System

-- Insert test users
INSERT INTO users (reference_id, username, email, password_hash, phone, user_type) VALUES
    (gen_random_uuid(), 'admin', 'admin@redbus.com', '$2a$10$YourHashedPasswordHere', '9999999999', 'AGENCY_ADMIN'),
    (gen_random_uuid(), 'agency1', 'agency1@redbus.com', '$2a$10$YourHashedPasswordHere', '9999999998', 'AGENCY_ADMIN'),
    (gen_random_uuid(), 'agency2', 'agency2@redbus.com', '$2a$10$YourHashedPasswordHere', '9999999997', 'AGENCY_ADMIN'),
    (gen_random_uuid(), 'customer1', 'customer1@gmail.com', '$2a$10$YourHashedPasswordHere', '8888888888', 'CUSTOMER'),
    (gen_random_uuid(), 'customer2', 'customer2@gmail.com', '$2a$10$YourHashedPasswordHere', '8888888887', 'CUSTOMER'),
    (gen_random_uuid(), 'customer3', 'customer3@gmail.com', '$2a$10$YourHashedPasswordHere', '8888888886', 'CUSTOMER');

-- Insert test agencies
INSERT INTO agencies (reference_id, name, contact_email, contact_phone, address, created_by_id) VALUES
    (gen_random_uuid(), 'RedBus Travels', 'contact@redbustravels.com', '1234567890', '123 Main St, Bangalore', 1),
    (gen_random_uuid(), 'Express Bus Service', 'info@expressbus.com', '1234567891', '456 MG Road, Delhi', 2),
    (gen_random_uuid(), 'Luxury Coaches', 'support@luxurycoaches.com', '1234567892', '789 Park Ave, Mumbai', 3);

-- Insert test vehicles
INSERT INTO vehicles (reference_id, agency_id, registration_number, capacity, vehicle_type, amenities, seat_layout) VALUES
    (gen_random_uuid(), 1, 'KA-01-AB-1234', 40, 'AC_SLEEPER', '{"wifi": true, "charging_points": true, "water_bottle": true}'::jsonb, '{"rows": 10, "seats_per_row": 4}'::jsonb),
    (gen_random_uuid(), 1, 'KA-01-AB-1235', 50, 'NON_AC_SEATER', '{"water_bottle": true}'::jsonb, '{"rows": 10, "seats_per_row": 5}'::jsonb),
    (gen_random_uuid(), 2, 'DL-02-CD-5678', 35, 'AC_SLEEPER', '{"wifi": true, "charging_points": true, "blanket": true}'::jsonb, '{"rows": 7, "seats_per_row": 5}'::jsonb),
    (gen_random_uuid(), 2, 'DL-02-CD-5679', 45, 'AC_SEATER', '{"wifi": true, "water_bottle": true}'::jsonb, '{"rows": 9, "seats_per_row": 5}'::jsonb),
    (gen_random_uuid(), 3, 'MH-03-EF-9012', 30, 'LUXURY_SLEEPER', '{"wifi": true, "charging_points": true, "meals": true, "entertainment": true}'::jsonb, '{"rows": 6, "seats_per_row": 5}'::jsonb);

-- Insert test stops (major cities)
INSERT INTO stops (reference_id, stop_name, city, state, latitude, longitude) VALUES
    (gen_random_uuid(), 'Majestic Bus Stand', 'Bangalore', 'Karnataka', 12.9766, 77.5993),
    (gen_random_uuid(), 'Electronic City', 'Bangalore', 'Karnataka', 12.8456, 77.6601),
    (gen_random_uuid(), 'Chennai Central', 'Chennai', 'Tamil Nadu', 13.0827, 80.2707),
    (gen_random_uuid(), 'Koyambedu Bus Terminal', 'Chennai', 'Tamil Nadu', 13.0694, 80.1953),
    (gen_random_uuid(), 'Mysore Bus Stand', 'Mysore', 'Karnataka', 12.3051, 76.6551),
    (gen_random_uuid(), 'Hyderabad Bus Station', 'Hyderabad', 'Telangana', 17.3850, 78.4867),
    (gen_random_uuid(), 'Mumbai Central', 'Mumbai', 'Maharashtra', 19.0760, 72.8777),
    (gen_random_uuid(), 'Pune Bus Terminal', 'Pune', 'Maharashtra', 18.5204, 73.8567),
    (gen_random_uuid(), 'Delhi ISBT', 'Delhi', 'Delhi', 28.6139, 77.2090),
    (gen_random_uuid(), 'Gurgaon Bus Stand', 'Gurgaon', 'Haryana', 28.4595, 77.0266);

-- Insert test routes
INSERT INTO routes (reference_id, agency_id, route_name, is_active) VALUES
    (gen_random_uuid(), 1, 'Bangalore to Chennai Express', true),
    (gen_random_uuid(), 1, 'Bangalore to Mysore', true),
    (gen_random_uuid(), 2, 'Delhi to Gurgaon Shuttle', true),
    (gen_random_uuid(), 2, 'Delhi to Mumbai Express', true),
    (gen_random_uuid(), 3, 'Mumbai to Pune Premium', true);

-- Insert route_stops (defining the sequence of stops for each route)
-- Route 1: Bangalore to Chennai (stops at Electronic City)
INSERT INTO route_stops (reference_id, route_id, stop_id, stop_sequence) VALUES
    (gen_random_uuid(), 1, 1, 1),  -- Majestic Bus Stand
    (gen_random_uuid(), 1, 2, 2),  -- Electronic City
    (gen_random_uuid(), 1, 3, 3);  -- Chennai Central

-- Route 2: Bangalore to Mysore
INSERT INTO route_stops (reference_id, route_id, stop_id, stop_sequence) VALUES
    (gen_random_uuid(), 2, 1, 1),  -- Majestic Bus Stand
    (gen_random_uuid(), 2, 5, 2);  -- Mysore Bus Stand

-- Route 3: Delhi to Gurgaon
INSERT INTO route_stops (reference_id, route_id, stop_id, stop_sequence) VALUES
    (gen_random_uuid(), 3, 9, 1),  -- Delhi ISBT
    (gen_random_uuid(), 3, 10, 2); -- Gurgaon Bus Stand

-- Route 4: Delhi to Mumbai (via Gurgaon)
INSERT INTO route_stops (reference_id, route_id, stop_id, stop_sequence) VALUES
    (gen_random_uuid(), 4, 9, 1),  -- Delhi ISBT
    (gen_random_uuid(), 4, 10, 2), -- Gurgaon Bus Stand
    (gen_random_uuid(), 4, 7, 3);  -- Mumbai Central

-- Route 5: Mumbai to Pune
INSERT INTO route_stops (reference_id, route_id, stop_id, stop_sequence) VALUES
    (gen_random_uuid(), 5, 7, 1),  -- Mumbai Central
    (gen_random_uuid(), 5, 8, 2);  -- Pune Bus Terminal

-- Insert test journeys (scheduled for various times)
-- Using CURRENT_DATE to make the data relevant whenever the seed is run
INSERT INTO journeys (reference_id, route_id, vehicle_id, source_stop_id, destination_stop_id, departure_time, arrival_time, base_fare, available_seats, status) VALUES
    -- Bangalore to Chennai journeys
    (gen_random_uuid(), 1, 1, 1, 3, CURRENT_DATE + INTERVAL '1 day' + TIME '06:00', CURRENT_DATE + INTERVAL '1 day' + TIME '12:00', 800.00, 40, 'SCHEDULED'),
    (gen_random_uuid(), 1, 1, 1, 3, CURRENT_DATE + INTERVAL '1 day' + TIME '10:00', CURRENT_DATE + INTERVAL '1 day' + TIME '16:00', 850.00, 40, 'SCHEDULED'),
    (gen_random_uuid(), 1, 2, 1, 3, CURRENT_DATE + INTERVAL '1 day' + TIME '14:00', CURRENT_DATE + INTERVAL '1 day' + TIME '20:00', 650.00, 50, 'SCHEDULED'),
    (gen_random_uuid(), 1, 1, 2, 3, CURRENT_DATE + INTERVAL '1 day' + TIME '08:00', CURRENT_DATE + INTERVAL '1 day' + TIME '13:00', 750.00, 40, 'SCHEDULED'),
    
    -- Bangalore to Mysore journeys
    (gen_random_uuid(), 2, 2, 1, 5, CURRENT_DATE + INTERVAL '1 day' + TIME '07:00', CURRENT_DATE + INTERVAL '1 day' + TIME '10:00', 350.00, 50, 'SCHEDULED'),
    (gen_random_uuid(), 2, 2, 1, 5, CURRENT_DATE + INTERVAL '1 day' + TIME '15:00', CURRENT_DATE + INTERVAL '1 day' + TIME '18:00', 350.00, 50, 'SCHEDULED'),
    
    -- Delhi to Gurgaon journeys
    (gen_random_uuid(), 3, 3, 9, 10, CURRENT_DATE + INTERVAL '1 day' + TIME '08:00', CURRENT_DATE + INTERVAL '1 day' + TIME '09:30', 150.00, 35, 'SCHEDULED'),
    (gen_random_uuid(), 3, 4, 9, 10, CURRENT_DATE + INTERVAL '1 day' + TIME '17:00', CURRENT_DATE + INTERVAL '1 day' + TIME '18:30', 150.00, 45, 'SCHEDULED'),
    
    -- Delhi to Mumbai journey
    (gen_random_uuid(), 4, 3, 9, 7, CURRENT_DATE + INTERVAL '1 day' + TIME '20:00', CURRENT_DATE + INTERVAL '2 days' + TIME '18:00', 2500.00, 35, 'SCHEDULED'),
    
    -- Mumbai to Pune journeys
    (gen_random_uuid(), 5, 5, 7, 8, CURRENT_DATE + INTERVAL '1 day' + TIME '09:00', CURRENT_DATE + INTERVAL '1 day' + TIME '12:30', 500.00, 30, 'SCHEDULED'),
    (gen_random_uuid(), 5, 5, 7, 8, CURRENT_DATE + INTERVAL '1 day' + TIME '16:00', CURRENT_DATE + INTERVAL '1 day' + TIME '19:30', 500.00, 30, 'SCHEDULED');

-- Insert some test bookings and tickets (optional - can be used for testing)
-- These would normally be created through the application
INSERT INTO bookings (reference_id, customer_id, journey_id, booking_status, total_fare, lock_expiry) VALUES
    (gen_random_uuid(), 4, 1, 'CONFIRMED', 1600.00, NULL),
    (gen_random_uuid(), 5, 5, 'CONFIRMED', 700.00, NULL);

-- Insert test tickets
INSERT INTO tickets (reference_id, booking_id, pnr, passenger_name, passenger_age, seat_number, fare) VALUES
    (gen_random_uuid(), 1, 'PNR1234567', 'John Doe', 30, 'A1', 800.00),
    (gen_random_uuid(), 1, 'PNR1234568', 'Jane Doe', 28, 'A2', 800.00),
    (gen_random_uuid(), 2, 'PNR2345678', 'Bob Smith', 35, 'B1', 350.00),
    (gen_random_uuid(), 2, 'PNR2345679', 'Alice Smith', 32, 'B2', 350.00);

-- Insert test payments
INSERT INTO payments (reference_id, booking_id, amount, payment_status, payment_method, transaction_id) VALUES
    (gen_random_uuid(), 1, 1600.00, 'SUCCESS', 'CREDIT_CARD', 'TXN123456789'),
    (gen_random_uuid(), 2, 700.00, 'SUCCESS', 'UPI', 'TXN987654321');
