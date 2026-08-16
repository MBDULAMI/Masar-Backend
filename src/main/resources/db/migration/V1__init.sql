CREATE TABLE passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    national_id VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    mobile VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    wallet_balance DECIMAL(10,2) NOT NULL
);

CREATE TABLE operators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    owner_passenger_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_operator_owner FOREIGN KEY (owner_passenger_id) REFERENCES passengers(id)
);

CREATE TABLE trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id BIGINT NOT NULL,
    origin_city VARCHAR(255) NOT NULL,
    destination_city VARCHAR(255) NOT NULL,
    departure_time TIMESTAMP WITH TIME ZONE NOT NULL,
    arrival_time TIMESTAMP WITH TIME ZONE NOT NULL,
    bus_code VARCHAR(255) NOT NULL,
    total_seats INT NOT NULL,
    seat_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_trip_operator FOREIGN KEY (operator_id) REFERENCES operators(id)
);

CREATE INDEX idx_trips_origin ON trips(origin_city);
CREATE INDEX idx_trips_destination ON trips(destination_city);
CREATE INDEX idx_trips_departure ON trips(departure_time);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    passenger_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    seat_count INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_booking_passenger FOREIGN KEY (passenger_id) REFERENCES passengers(id),
    CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trips(id)
);

CREATE INDEX idx_bookings_passenger ON bookings(passenger_id);
CREATE INDEX idx_bookings_trip ON bookings(trip_id);

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_number INT NOT NULL,
    serial VARCHAR(255) NOT NULL,
    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_tickets_booking ON tickets(booking_id);

CREATE TABLE wallet_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    passenger_id BIGINT NOT NULL,
    booking_id BIGINT,
    CONSTRAINT fk_wallet_passenger FOREIGN KEY (passenger_id) REFERENCES passengers(id),
    CONSTRAINT fk_wallet_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_wallet_passenger ON wallet_transactions(passenger_id);