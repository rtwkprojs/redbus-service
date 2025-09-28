-- Create agencies table
CREATE TABLE IF NOT EXISTS agencies (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    agency_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) NOT NULL UNIQUE,
    contact_phone VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT true,
    owner_reference_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    reference_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    total_seats INTEGER NOT NULL,
    manufacturer VARCHAR(50),
    model VARCHAR(50),
    year_of_manufacture INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    has_ac BOOLEAN NOT NULL DEFAULT false,
    has_wifi BOOLEAN NOT NULL DEFAULT false,
    has_charging_points BOOLEAN NOT NULL DEFAULT false,
    agency_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_vehicle_agency FOREIGN KEY (agency_id) REFERENCES agencies(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_agencies_reference_id ON agencies(reference_id);
CREATE INDEX idx_agencies_owner_reference_id ON agencies(owner_reference_id);
CREATE INDEX idx_agencies_is_active ON agencies(is_active);
CREATE INDEX idx_agencies_contact_email ON agencies(contact_email);
CREATE INDEX idx_agencies_contact_phone ON agencies(contact_phone);

CREATE INDEX idx_vehicles_reference_id ON vehicles(reference_id);
CREATE INDEX idx_vehicles_agency_id ON vehicles(agency_id);
CREATE INDEX idx_vehicles_registration_number ON vehicles(registration_number);
CREATE INDEX idx_vehicles_vehicle_type ON vehicles(vehicle_type);
CREATE INDEX idx_vehicles_is_active ON vehicles(is_active);
