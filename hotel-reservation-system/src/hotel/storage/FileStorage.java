package hotel.storage;

import hotel.model.Guest;
import hotel.model.Payment;
import hotel.model.PaymentMethod;
import hotel.model.PaymentStatus;
import hotel.model.Reservation;
import hotel.model.ReservationStatus;
import hotel.model.Room;
import hotel.model.RoomType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persists hotel data as CSV files so records survive restarts.
 * Pipe characters in text fields are replaced to keep rows simple.
 */
public class FileStorage {
    private final Path dataDir;
    private final Path roomsFile;
    private final Path guestsFile;
    private final Path reservationsFile;
    private final Path paymentsFile;

    public FileStorage(Path dataDir) {
        this.dataDir = dataDir;
        this.roomsFile = dataDir.resolve("rooms.csv");
        this.guestsFile = dataDir.resolve("guests.csv");
        this.reservationsFile = dataDir.resolve("reservations.csv");
        this.paymentsFile = dataDir.resolve("payments.csv");
    }

    public void ensureReady() throws IOException {
        Files.createDirectories(dataDir);
        if (!Files.exists(roomsFile)) {
            writeLines(roomsFile, List.of(
                    "roomNumber,roomType,pricePerNight,available",
                    "101,STANDARD,2500.00,true",
                    "102,STANDARD,2500.00,true",
                    "103,STANDARD,2800.00,true",
                    "201,DELUXE,4500.00,true",
                    "202,DELUXE,4800.00,true",
                    "203,DELUXE,5000.00,true",
                    "301,SUITE,8000.00,true",
                    "302,SUITE,9500.00,true"
            ));
        }
        if (!Files.exists(guestsFile)) {
            writeLines(guestsFile, List.of("guestId,name,phone,email"));
        }
        if (!Files.exists(reservationsFile)) {
            writeLines(reservationsFile, List.of(
                    "bookingId,guestId,roomNumber,checkIn,checkOut,reservationStatus,paymentStatus"));
        }
        if (!Files.exists(paymentsFile)) {
            writeLines(paymentsFile, List.of(
                    "paymentId,bookingId,amount,method,status,paidAt,receiptNote"));
        }
    }

    public List<Room> loadRooms() throws IOException {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(roomsFile, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                int number = Integer.parseInt(parts[0]);
                RoomType type = RoomType.valueOf(parts[1]);
                double price = Double.parseDouble(parts[2]);
                boolean available = Boolean.parseBoolean(parts[3]);
                rooms.add(Room.create(number, type, price, available));
            }
        }
        return rooms;
    }

    public Map<String, Guest> loadGuests() throws IOException {
        Map<String, Guest> guests = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(guestsFile, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                Guest guest = new Guest(parts[0], unescape(parts[1]), parts[2], parts[3]);
                guests.put(guest.getGuestId(), guest);
            }
        }
        return guests;
    }

    public List<Reservation> loadReservations(Map<Integer, Room> roomsByNumber,
                                              Map<String, Guest> guests) throws IOException {
        List<Reservation> reservations = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(reservationsFile, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                Guest guest = guests.get(parts[1]);
                Room room = roomsByNumber.get(Integer.parseInt(parts[2]));
                if (guest == null || room == null) {
                    continue;
                }
                reservations.add(new Reservation(
                        parts[0],
                        guest,
                        room,
                        LocalDate.parse(parts[3]),
                        LocalDate.parse(parts[4]),
                        ReservationStatus.valueOf(parts[5]),
                        PaymentStatus.valueOf(parts[6])
                ));
            }
        }
        return reservations;
    }

    public List<Payment> loadPayments() throws IOException {
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(paymentsFile, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                payments.add(new Payment(
                        parts[0],
                        parts[1],
                        Double.parseDouble(parts[2]),
                        PaymentMethod.valueOf(parts[3]),
                        PaymentStatus.valueOf(parts[4]),
                        LocalDateTime.parse(parts[5]),
                        unescape(parts[6])
                ));
            }
        }
        return payments;
    }

    public void saveAll(List<Room> rooms, Map<String, Guest> guests,
                        List<Reservation> reservations, List<Payment> payments) throws IOException {
        List<String> roomLines = new ArrayList<>();
        roomLines.add("roomNumber,roomType,pricePerNight,available");
        for (Room room : rooms) {
            roomLines.add(String.format("%d,%s,%.2f,%s",
                    room.getRoomNumber(), room.getRoomType().name(),
                    room.getPricePerNight(), room.isAvailable()));
        }
        writeLines(roomsFile, roomLines);

        List<String> guestLines = new ArrayList<>();
        guestLines.add("guestId,name,phone,email");
        for (Guest guest : guests.values()) {
            guestLines.add(String.join(",",
                    guest.getGuestId(),
                    escape(guest.getName()),
                    guest.getPhone(),
                    guest.getEmail()));
        }
        writeLines(guestsFile, guestLines);

        List<String> reservationLines = new ArrayList<>();
        reservationLines.add("bookingId,guestId,roomNumber,checkIn,checkOut,reservationStatus,paymentStatus");
        for (Reservation reservation : reservations) {
            reservationLines.add(String.join(",",
                    reservation.getBookingId(),
                    reservation.getGuest().getGuestId(),
                    String.valueOf(reservation.getRoom().getRoomNumber()),
                    reservation.getCheckIn().toString(),
                    reservation.getCheckOut().toString(),
                    reservation.getReservationStatus().name(),
                    reservation.getPaymentStatus().name()));
        }
        writeLines(reservationsFile, reservationLines);

        List<String> paymentLines = new ArrayList<>();
        paymentLines.add("paymentId,bookingId,amount,method,status,paidAt,receiptNote");
        for (Payment payment : payments) {
            paymentLines.add(String.join(",",
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    String.format("%.2f", payment.getAmount()),
                    payment.getMethod().name(),
                    payment.getStatus().name(),
                    payment.getPaidAt().toString(),
                    escape(payment.getReceiptNote())));
        }
        writeLines(paymentsFile, paymentLines);
    }

    private void writeLines(Path file, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", ";").replace("|", "/");
    }

    private String unescape(String value) {
        return value == null ? "" : value.replace(";", ",");
    }
}
