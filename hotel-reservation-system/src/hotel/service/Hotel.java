package hotel.service;

import hotel.model.Guest;
import hotel.model.Payment;
import hotel.model.PaymentMethod;
import hotel.model.PaymentStatus;
import hotel.model.Reservation;
import hotel.model.ReservationStatus;
import hotel.model.Room;
import hotel.model.RoomType;
import hotel.storage.FileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Central service that manages rooms, guests, reservations, and payments.
 */
public class Hotel {
    private final FileStorage storage;
    private final List<Room> rooms = new ArrayList<>();
    private final Map<String, Guest> guests = new LinkedHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private int bookingSequence = 1000;
    private int guestSequence = 100;
    private int paymentSequence = 500;

    public Hotel(Path dataDir) throws IOException {
        this.storage = new FileStorage(dataDir);
        storage.ensureReady();
        load();
    }

    private void load() throws IOException {
        rooms.clear();
        rooms.addAll(storage.loadRooms());
        guests.clear();
        guests.putAll(storage.loadGuests());
        Map<Integer, Room> roomsByNumber = new LinkedHashMap<>();
        for (Room room : rooms) {
            roomsByNumber.put(room.getRoomNumber(), room);
        }
        reservations.clear();
        reservations.addAll(storage.loadReservations(roomsByNumber, guests));
        payments.clear();
        payments.addAll(storage.loadPayments());
        refreshRoomAvailability();
        bookingSequence = nextSequence(reservations.stream()
                .map(Reservation::getBookingId)
                .toList(), "BK", 1000);
        guestSequence = nextSequence(new ArrayList<>(guests.keySet()), "G", 100);
        paymentSequence = nextSequence(payments.stream()
                .map(Payment::getPaymentId)
                .toList(), "PAY", 500);
    }

    private int nextSequence(List<String> ids, String prefix, int defaultStart) {
        int max = defaultStart;
        for (String id : ids) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // skip malformed historical ids
                }
            }
        }
        return max;
    }

    public synchronized void save() throws IOException {
        storage.saveAll(rooms, guests, reservations, payments);
    }

    public List<Room> getAllRooms() {
        List<Room> copy = new ArrayList<>(rooms);
        copy.sort(Comparator.comparingInt(Room::getRoomNumber));
        return copy;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public Optional<Room> findRoom(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == roomNumber)
                .findFirst();
    }

    public Optional<Reservation> findReservation(String bookingId) {
        return reservations.stream()
                .filter(item -> item.getBookingId().equalsIgnoreCase(bookingId.trim()))
                .findFirst();
    }

    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut,
                                           RoomType type, Double maxBudget) {
        List<Room> matches = new ArrayList<>();
        for (Room room : getAllRooms()) {
            if (type != null && room.getRoomType() != type) {
                continue;
            }
            if (maxBudget != null && room.getPricePerNight() > maxBudget) {
                continue;
            }
            if (isRoomFree(room, checkIn, checkOut, null)) {
                matches.add(room);
            }
        }
        return matches;
    }

    public boolean isRoomFree(Room room, LocalDate checkIn, LocalDate checkOut, String ignoreBookingId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
                continue;
            }
            if (reservation.getRoom().getRoomNumber() != room.getRoomNumber()) {
                continue;
            }
            if (ignoreBookingId != null && reservation.getBookingId().equals(ignoreBookingId)) {
                continue;
            }
            if (reservation.overlaps(checkIn, checkOut)) {
                return false;
            }
        }
        return true;
    }

    public Reservation bookRoom(String name, String phone, String email, int roomNumber,
                                LocalDate checkIn, LocalDate checkOut) {
        Room room = findRoom(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Room " + roomNumber + " does not exist."));
        if (!isRoomFree(room, checkIn, checkOut, null)) {
            throw new IllegalStateException("Room " + roomNumber + " is already booked for those dates.");
        }

        String guestId = "G" + (++guestSequence);
        Guest guest = new Guest(guestId, name, phone, email);
        guests.put(guestId, guest);

        String bookingId = "BK" + (++bookingSequence);
        Reservation reservation = new Reservation(
                bookingId, guest, room, checkIn, checkOut,
                ReservationStatus.CONFIRMED, PaymentStatus.PENDING);
        reservations.add(reservation);
        refreshRoomAvailability();
        return reservation;
    }

    public Reservation cancelReservation(String bookingId) {
        Reservation reservation = findReservation(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No reservation found for booking ID " + bookingId));
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("This reservation is already cancelled.");
        }
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        refreshRoomAvailability();
        return reservation;
    }

    public Payment makePayment(String bookingId, PaymentMethod method, String extraDetails) {
        Reservation reservation = findReservation(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No reservation found for booking ID " + bookingId));
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled reservation.");
        }
        if (reservation.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("This booking is already paid.");
        }

        boolean success = simulatePayment(method, extraDetails);
        PaymentStatus status = success ? PaymentStatus.PAID : PaymentStatus.FAILED;
        String note = success
                ? "Payment approved (simulation)."
                : "Payment declined (simulation). Please try another method.";
        Payment payment = new Payment(
                "PAY" + (++paymentSequence),
                reservation.getBookingId(),
                reservation.getTotalAmount(),
                method,
                status,
                LocalDateTime.now(),
                note + " " + extraDetails
        );
        payments.add(payment);
        if (success) {
            reservation.setPaymentStatus(PaymentStatus.PAID);
        } else {
            reservation.setPaymentStatus(PaymentStatus.FAILED);
        }
        return payment;
    }

    /**
     * Simulated gateway rules (no real money):
     * - CASH always succeeds.
     * - UPI succeeds when the ID contains '@'.
     * - CARD succeeds for 16-digit numbers that do not start with 0000.
     */
    private boolean simulatePayment(PaymentMethod method, String extraDetails) {
        String details = extraDetails == null ? "" : extraDetails.trim();
        switch (method) {
            case CASH:
                return true;
            case UPI:
                return details.contains("@") && details.length() >= 5;
            case CARD:
                String digits = details.replaceAll("\\s+", "");
                return digits.matches("\\d{16}") && !digits.startsWith("0000");
            default:
                return false;
        }
    }

    /**
     * A room is marked available when it has no active (non-cancelled) reservations.
     * Date-specific availability is still checked during search and booking.
     */
    public void refreshRoomAvailability() {
        for (Room room : rooms) {
            boolean hasActive = reservations.stream()
                    .anyMatch(item -> item.getRoom().getRoomNumber() == room.getRoomNumber()
                            && item.getReservationStatus() == ReservationStatus.CONFIRMED);
            room.setAvailable(!hasActive);
        }
    }
}
