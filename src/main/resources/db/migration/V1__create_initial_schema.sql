-- Imara: Multi-tenant building apartment bills management
-- Initial schema with apartments, service types, bills, and subscriptions

-- Enable UUID extension for uuid_generate_v4() (PostgreSQL < 13 compatibility)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Use imara_schema for all application tables
CREATE SCHEMA IF NOT EXISTS imara_schema;
SET search_path TO imara_schema, public;

-- 1. Companies (tenant root)
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_companies_email UNIQUE (email)
);

-- 2. Users (with soft delete)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('COMPANY_OWNER', 'MAINTAINER'))
);

CREATE UNIQUE INDEX idx_users_one_owner_per_company
    ON users (company_id)
    WHERE role = 'COMPANY_OWNER';

CREATE INDEX idx_users_company_id ON users (company_id);

-- 3. Buildings
CREATE TABLE buildings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_buildings_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_buildings_company_id ON buildings (company_id);

-- 4. Apartments (with composite unique for bills FK)
CREATE TABLE apartments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    building_id UUID NOT NULL,
    number VARCHAR(50) NOT NULL,
    floor INTEGER,
    area DECIMAL(10, 2),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_apartments_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_apartments_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT uq_apartments_building_number UNIQUE (building_id, number),
    CONSTRAINT uq_apartments_building_id UNIQUE (building_id, id)
);

CREATE INDEX idx_apartments_company_id ON apartments (company_id);
CREATE INDEX idx_apartments_building_id ON apartments (building_id);

-- 5. Service types (company-owned catalog, with soft delete)
CREATE TABLE service_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_types_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT uq_service_types_company_name UNIQUE (company_id, name)
);

CREATE INDEX idx_service_types_company_id ON service_types (company_id);

-- 6. Building service types (with soft delete)
CREATE TABLE building_service_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    building_id UUID NOT NULL,
    service_type_id UUID NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    is_optional BOOLEAN NOT NULL DEFAULT false,
    billing_period VARCHAR(20) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_building_service_types_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_building_service_types_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_building_service_types_service FOREIGN KEY (service_type_id) REFERENCES service_types(id),
    CONSTRAINT uq_building_service_types_building_service UNIQUE (building_id, service_type_id),
    CONSTRAINT chk_building_service_types_period CHECK (billing_period IN ('MONTHLY', 'YEARLY'))
);

CREATE INDEX idx_building_service_types_company_id ON building_service_types (company_id);
CREATE INDEX idx_building_service_types_building_id ON building_service_types (building_id);

-- 7. Building assignments (Maintainer <-> Building)
CREATE TABLE building_assignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    building_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_building_assignments_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_building_assignments_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_building_assignments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_building_assignments_building_user UNIQUE (building_id, user_id)
);

CREATE INDEX idx_building_assignments_company_id ON building_assignments (company_id);
CREATE INDEX idx_building_assignments_user_id ON building_assignments (user_id);

-- 8. Apartment service subscriptions (optional services - which apartments subscribed)
CREATE TABLE apartment_service_subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    apartment_id UUID NOT NULL,
    building_service_type_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_apartment_subscriptions_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_apartment_subscriptions_apartment FOREIGN KEY (apartment_id) REFERENCES apartments(id),
    CONSTRAINT fk_apartment_subscriptions_building_service FOREIGN KEY (building_service_type_id) REFERENCES building_service_types(id),
    CONSTRAINT uq_apartment_subscriptions_apartment_service UNIQUE (apartment_id, building_service_type_id)
);

CREATE INDEX idx_apartment_subscriptions_company_id ON apartment_service_subscriptions (company_id);
CREATE INDEX idx_apartment_subscriptions_apartment_id ON apartment_service_subscriptions (apartment_id);

-- Trigger: ensure apartment belongs to building of the service type
CREATE OR REPLACE FUNCTION check_apartment_subscription_building()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT building_id FROM apartments WHERE id = NEW.apartment_id) !=
       (SELECT building_id FROM building_service_types WHERE id = NEW.building_service_type_id) THEN
        RAISE EXCEPTION 'Apartment must belong to the building of the service type';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_apartment_subscription_building
    BEFORE INSERT OR UPDATE ON apartment_service_subscriptions
    FOR EACH ROW
    EXECUTE PROCEDURE check_apartment_subscription_building();

-- 9. User apartments (user <-> apartment many-to-many)
CREATE TABLE user_apartments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    user_id UUID NOT NULL,
    apartment_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_apartments_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_user_apartments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_apartments_apartment FOREIGN KEY (apartment_id) REFERENCES apartments(id),
    CONSTRAINT uq_user_apartments_user_apartment UNIQUE (user_id, apartment_id)
);

CREATE INDEX idx_user_apartments_company_id ON user_apartments (company_id);
CREATE INDEX idx_user_apartments_user_id ON user_apartments (user_id);
CREATE INDEX idx_user_apartments_apartment_id ON user_apartments (apartment_id);

-- 10. Bills
CREATE TABLE bills (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL,
    building_id UUID NOT NULL,
    apartment_id UUID NOT NULL,
    service_type_id UUID NOT NULL,
    billing_period_start DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    due_date DATE,
    issued_at DATE,
    paid_at DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bills_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_bills_building_apartment FOREIGN KEY (building_id, apartment_id) REFERENCES apartments(building_id, id),
    CONSTRAINT fk_bills_service_type FOREIGN KEY (service_type_id) REFERENCES service_types(id),
    CONSTRAINT uq_bills_apartment_service_period UNIQUE (apartment_id, service_type_id, billing_period_start),
    CONSTRAINT chk_bills_status CHECK (status IN ('PENDING', 'PAID'))
);

CREATE INDEX idx_bills_company_id ON bills (company_id);
CREATE INDEX idx_bills_apartment_id ON bills (apartment_id);
CREATE INDEX idx_bills_status ON bills (status);
CREATE INDEX idx_bills_company_status ON bills (company_id, status);
CREATE INDEX idx_bills_apartment_status ON bills (apartment_id, status);
CREATE INDEX idx_bills_company_due_date ON bills (company_id, due_date);
