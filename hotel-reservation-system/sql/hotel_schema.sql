-- Optional MySQL schema for the Hotel Reservation System.
-- The Java application uses CSV file storage by default so it can run
-- without installing MySQL. Use these statements if you later connect JDBC.

CREATE DATABASE IF NOT EXISTS hotel_reservation;
USE hotel_reservation;

CREATE TABLE IF NOT EXISTS rooms (
    room_number INT PRIMARY KEY,
    room_type ENUM('STANDARD', 'DELUXE', 'SUITE') NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS guests (
    guest_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    booking_id VARCHAR(20) PRIMARY KEY,
    guest_id VARCHAR(20) NOT NULL,
    room_number INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    reservation_status ENUM('CONFIRMED', 'CANCELLED') NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'FAILED') NOT NULL,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_number) REFERENCES rooms(room_number)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(20) PRIMARY KEY,
    booking_id VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    method ENUM('UPI', 'CARD', 'CASH') NOT NULL,
    status ENUM('PENDING', 'PAID', 'FAILED') NOT NULL,
    paid_at DATETIME NOT NULL,
    receipt_note VARCHAR(255),
    FOREIGN KEY (booking_id) REFERENCES reservations(booking_id)
);

INSERT INTO rooms (room_number, room_type, price_per_night, available) VALUES
    (101, 'STANDARD', 2500.00, TRUE),
    (102, 'STANDARD', 2500.00, TRUE),
    (103, 'STANDARD', 2800.00, TRUE),
    (201, 'DELUXE', 4500.00, TRUE),
    (202, 'DELUXE', 4800.00, TRUE),
    (203, 'DELUXE', 5000.00, TRUE),
    (301, 'SUITE', 8000.00, TRUE),
    (302, 'SUITE', 9500.00, TRUE);
