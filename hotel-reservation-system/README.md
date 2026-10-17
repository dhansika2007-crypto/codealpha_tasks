# Sunrise Hotel Reservation System

Beginner-friendly Java console application for searching rooms, booking stays, simulating payments, cancelling reservations, and viewing booking details. Data is stored in CSV files so records remain after the program is closed.

## Features

1. View all rooms (Standard, Deluxe, Suite)
2. Search available rooms by dates, category, and budget
3. Book a room with guest details and a unique booking ID
4. Cancel a reservation
5. View booking details
6. Simulated payment (UPI, card, cash) with a receipt
7. Admin list of all reservations
8. Exit and save

Double booking of the same room for overlapping dates is blocked.

## Project structure

```
hotel-reservation-system/
  src/hotel/Main.java
  src/hotel/model/          Room, Guest, Reservation, Payment, enums, subclasses
  src/hotel/service/Hotel.java
  src/hotel/storage/FileStorage.java
  src/hotel/util/InputHelper.java
  sql/hotel_schema.sql      Optional MySQL schema
  data/                     Created on first run (CSV persistence)
  run.bat
```

## Requirements

- Java JDK 11 or later (`java` and `javac` on PATH)

MySQL is **not required**. File storage is used by default. `sql/hotel_schema.sql` is included if you later want JDBC.

## How to run (Windows)

From this folder:

```bat
run.bat
```

Or manually:

```bat
mkdir bin
javac -d bin src\hotel\model\*.java src\hotel\storage\*.java src\hotel\service\*.java src\hotel\util\*.java src\hotel\Main.java
java -cp bin hotel.Main
```

On macOS or Linux, use `/` instead of `\` in those paths.

## Sample rooms (created on first run)

| Room | Type     | Price per night |
|------|----------|-----------------|
| 101  | Standard | Rs. 2500        |
| 102  | Standard | Rs. 2500        |
| 103  | Standard | Rs. 2800        |
| 201  | Deluxe   | Rs. 4500        |
| 202  | Deluxe   | Rs. 4800        |
| 203  | Deluxe   | Rs. 5000        |
| 301  | Suite    | Rs. 8000        |
| 302  | Suite    | Rs. 9500        |

## Sample interaction

Dates below use **2026** so they stay valid for demos. Use dates that are today or later on your machine.

### Search available rooms

```
2
2026-12-20
2026-12-23
1
3000
```

Expected: Standard rooms 101–103 listed with prices and descriptions. Deluxe/Suite are excluded by budget.

### Book a room

```
3
Anita Sharma
9876543210
anita@example.com
101
2026-12-20
2026-12-23
```

Expected: Booking ID such as `BK1001`, 3 nights, total **Rs. 7500.00**, payment **PENDING**.

Trying to book room 101 again for `2026-12-22` to `2026-12-24` should fail (overlap). Booking from `2026-12-23` is allowed (check-out day is free).

### Make payment (success)

```
6
BK1001
1
anita@upi
```

Expected: receipt with status **PAID**.

To simulate failure, choose card and enter `0000111122223333`. Cash always succeeds.

### View booking details

```
5
BK1001
```

Shows guest, room, dates, nights, amount, payment status, and reservation status.

### Cancel reservation

```
4
BK1001
```

Expected: cancellation confirmation; room 101 can be booked again for those dates.

## OOP concepts

- **Encapsulation:** fields are private with getters/setters (`Guest`, `Room`, `Reservation`).
- **Constructors:** every model class is created with a constructor that sets identity and required data.
- **Inheritance:** `StandardRoom`, `DeluxeRoom`, and `SuiteRoom` extend `Room` and supply descriptions.
- **Abstraction:** `Room` is abstract; callers work with the `Room` type.
- **Enums:** room type, payment method, and statuses are type-safe.
- **Separation of concerns:** `Hotel` holds business rules, `FileStorage` handles files, `Main` only drives the menu.

## Persistence

CSV files under `data/`:

- `rooms.csv`
- `guests.csv`
- `reservations.csv`
- `payments.csv`

Close the app with menu option **8** (or complete a booking/payment/cancel) so data is written. Restart the app and the same booking IDs still work.

## Optional MySQL

If a database is required for a report, run `sql/hotel_schema.sql` in MySQL Workbench or the `mysql` client. The shipped Java code does not open a JDBC connection; it uses files so the project runs immediately in a lab without extra setup.
