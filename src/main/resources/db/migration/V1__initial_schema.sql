CREATE TABLE customers (
    id UUID PRIMARY KEY, name VARCHAR(140) NOT NULL, email VARCHAR(180) NOT NULL UNIQUE, phone VARCHAR(40), active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE service_sites (
    id UUID PRIMARY KEY, customer_id UUID NOT NULL REFERENCES customers(id), name VARCHAR(120) NOT NULL, address_line1 VARCHAR(180) NOT NULL,
    city VARCHAR(100) NOT NULL, state VARCHAR(80) NOT NULL, postal_code VARCHAR(20) NOT NULL, time_zone VARCHAR(80) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE assets (
    id UUID PRIMARY KEY, site_id UUID NOT NULL REFERENCES service_sites(id), name VARCHAR(120) NOT NULL, asset_type VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE, status VARCHAR(30) NOT NULL, installed_on DATE,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE technicians (
    id UUID PRIMARY KEY, full_name VARCHAR(140) NOT NULL, email VARCHAR(180) NOT NULL UNIQUE, active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE technician_skills (
    technician_id UUID NOT NULL REFERENCES technicians(id) ON DELETE CASCADE, skill VARCHAR(80) NOT NULL,
    PRIMARY KEY (technician_id, skill)
);
CREATE TABLE inventory_items (
    id UUID PRIMARY KEY, version BIGINT NOT NULL DEFAULT 0, sku VARCHAR(60) NOT NULL UNIQUE, name VARCHAR(160) NOT NULL,
    quantity_on_hand INTEGER NOT NULL, quantity_reserved INTEGER NOT NULL, reorder_point INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_inventory_nonnegative CHECK (quantity_on_hand >= 0 AND quantity_reserved >= 0 AND reorder_point >= 0),
    CONSTRAINT chk_inventory_reserved CHECK (quantity_reserved <= quantity_on_hand)
);
CREATE TABLE work_orders (
    id UUID PRIMARY KEY, version BIGINT NOT NULL DEFAULT 0, site_id UUID NOT NULL REFERENCES service_sites(id), asset_id UUID REFERENCES assets(id), technician_id UUID REFERENCES technicians(id),
    summary VARCHAR(180) NOT NULL, description VARCHAR(4000) NOT NULL, priority VARCHAR(30) NOT NULL, status VARCHAR(30) NOT NULL,
    resolution_notes VARCHAR(4000), completed_at TIMESTAMPTZ, cancelled_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE appointments (
    id UUID PRIMARY KEY, work_order_id UUID NOT NULL REFERENCES work_orders(id), technician_id UUID NOT NULL REFERENCES technicians(id),
    start_at TIMESTAMPTZ NOT NULL, end_at TIMESTAMPTZ NOT NULL, source_time_zone VARCHAR(80) NOT NULL, status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_appointment_time CHECK (end_at > start_at)
);
CREATE TABLE work_order_parts (
    id UUID PRIMARY KEY, work_order_id UUID NOT NULL REFERENCES work_orders(id), inventory_item_id UUID NOT NULL REFERENCES inventory_items(id),
    quantity INTEGER NOT NULL, status VARCHAR(30) NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_part_quantity CHECK (quantity > 0)
);
CREATE TABLE work_order_events (
    id UUID PRIMARY KEY, work_order_id UUID NOT NULL REFERENCES work_orders(id), event_type VARCHAR(60) NOT NULL,
    message VARCHAR(1000) NOT NULL, occurred_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_sites_customer ON service_sites(customer_id);
CREATE INDEX idx_assets_site ON assets(site_id);
CREATE INDEX idx_work_orders_status ON work_orders(status);
CREATE INDEX idx_work_orders_site ON work_orders(site_id);
CREATE INDEX idx_appointments_technician_time ON appointments(technician_id, start_at, end_at);
CREATE INDEX idx_work_order_parts_order ON work_order_parts(work_order_id);
CREATE INDEX idx_work_order_events_order_time ON work_order_events(work_order_id, occurred_at);
