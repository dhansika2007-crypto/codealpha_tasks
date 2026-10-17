package hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A booking that links a guest, a room, stay dates, cost, and status.
 */
public class Reservation {
    private final String bookingId;
    private final Guest guest;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final long nights;
    private final double totalAmount;
    private ReservationStatus reservationStatus;
    private PaymentStatus paymentStatus;

    public Reservation(String bookingId, Guest guest, Room room,
                       LocalDate checkIn, LocalDate checkOut,
                       ReservationStatus reservationStatus, PaymentStatus paymentStatus) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalAmount = nights * room.getPricePerNight();
        this.reservationStatus = reservationStatus;
        this.paymentStatus = paymentStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getNights() {
        return nights;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * True when this stay overlaps another date range on the same room.
     * Standard hotel rule: check-out day is free for a new check-in.
     */
    public boolean overlaps(LocalDate otherIn, LocalDate otherOut) {
        return checkIn.isBefore(otherOut) && otherIn.isBefore(checkOut);
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("Booking ID        : ").append(bookingId).append('\n');
        sb.append("Guest             : ").append(guest.getName()).append('\n');
        sb.append("Phone             : ").append(guest.getPhone()).append('\n');
        sb.append("Email             : ").append(guest.getEmail()).append('\n');
        sb.append("Room Number       : ").append(room.getRoomNumber()).append('\n');
        sb.append("Room Category     : ").append(room.getRoomType().getDisplayName()).append('\n');
        sb.append("Check-in          : ").append(checkIn).append('\n');
        sb.append("Check-out         : ").append(checkOut).append('\n');
        sb.append("Nights            : ").append(nights).append('\n');
        sb.append(String.format("Total Amount      : Rs. %.2f%n", totalAmount));
        sb.append("Payment Status    : ").append(paymentStatus).append('\n');
        sb.append("Reservation Status: ").append(reservationStatus).append('\n');
        sb.append("--------------------------------------------------");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s | Room %d (%s) | %s to %s | %s | Pay: %s | Rs. %.2f",
                bookingId, room.getRoomNumber(), room.getRoomType().getDisplayName(),
                checkIn, checkOut, reservationStatus, paymentStatus, totalAmount);
    }
}
