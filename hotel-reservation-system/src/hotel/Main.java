package hotel;

import hotel.model.Payment;
import hotel.model.PaymentMethod;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.RoomType;
import hotel.service.Hotel;
import hotel.util.InputHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven console application for the Hotel Reservation System.
 */
public class Main {
    private final Hotel hotel;
    private final InputHelper input;
    private boolean running = true;

    public Main(Hotel hotel, InputHelper input) {
        this.hotel = hotel;
        this.input = input;
    }

    public static void main(String[] args) {
        Path dataDir = Path.of("data");
        try (Scanner scanner = new Scanner(System.in)) {
            Hotel hotel = new Hotel(dataDir);
            Main app = new Main(hotel, new InputHelper(scanner));
            app.run();
        } catch (IOException ex) {
            System.out.println("Could not load hotel data: " + ex.getMessage());
        }
    }

    private void run() {
        System.out.println("==================================================");
        System.out.println("        SUNRISE HOTEL RESERVATION SYSTEM");
        System.out.println("==================================================");
        while (running) {
            printMenu();
            int choice = input.readInt("Enter your choice: ");
            try {
                handleChoice(choice);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Could not save data: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. View All Rooms");
        System.out.println("2. Search Available Rooms");
        System.out.println("3. Book a Room");
        System.out.println("4. Cancel Reservation");
        System.out.println("5. View Booking Details");
        System.out.println("6. Make Payment");
        System.out.println("7. View All Reservations (Admin)");
        System.out.println("8. Exit");
    }

    private void handleChoice(int choice) throws IOException {
        switch (choice) {
            case 1 -> viewAllRooms();
            case 2 -> searchRooms();
            case 3 -> bookRoom();
            case 4 -> cancelReservation();
            case 5 -> viewBookingDetails();
            case 6 -> makePayment();
            case 7 -> viewAllReservations();
            case 8 -> exit();
            default -> System.out.println("Please choose a number from 1 to 8.");
        }
    }

    private void viewAllRooms() {
        System.out.println("\n--- All Rooms ---");
        for (Room room : hotel.getAllRooms()) {
            System.out.println(room);
        }
    }

    private void searchRooms() {
        LocalDate checkIn = input.readDate("Check-in date");
        LocalDate checkOut = input.readDate("Check-out date");
        if (!input.isValidStay(checkIn, checkOut)) {
            return;
        }

        System.out.println("Room category: 1) Standard  2) Deluxe  3) Suite  4) Any");
        String typeChoice = input.readLine("Enter category: ");
        RoomType type = null;
        if (!typeChoice.equals("4") && !typeChoice.equalsIgnoreCase("any")) {
            type = RoomType.fromUserInput(typeChoice);
            if (type == null) {
                System.out.println("Unknown category. Searching all categories.");
            }
        }

        String budgetRaw = input.readLine("Maximum budget per night (blank for no limit): ");
        Double budget = null;
        if (!budgetRaw.isBlank()) {
            try {
                budget = Double.parseDouble(budgetRaw);
                if (budget < 0) {
                    System.out.println("Budget cannot be negative.");
                    return;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid budget amount.");
                return;
            }
        }

        List<Room> results = hotel.searchAvailableRooms(checkIn, checkOut, type, budget);
        if (results.isEmpty()) {
            System.out.println("No rooms match those dates, category, and budget.");
            return;
        }
        System.out.println("\n--- Available Rooms ---");
        for (Room room : results) {
            System.out.println(room);
        }
    }

    private void bookRoom() throws IOException {
        String name = input.readName("Guest name: ");
        String phone = input.readPhone("Phone (10 digits): ");
        String email = input.readEmail("Email: ");
        int roomNumber = input.readInt("Room number: ");
        LocalDate checkIn = input.readDate("Check-in date");
        LocalDate checkOut = input.readDate("Check-out date");
        if (!input.isValidStay(checkIn, checkOut)) {
            return;
        }

        Reservation reservation = hotel.bookRoom(name, phone, email, roomNumber, checkIn, checkOut);
        hotel.save();
        System.out.println("\nBooking confirmed!");
        System.out.println(reservation.toDetailedString());
        System.out.println("Please use option 6 to complete payment.");
    }

    private void cancelReservation() throws IOException {
        String bookingId = input.readLine("Enter booking ID: ");
        Reservation reservation = hotel.cancelReservation(bookingId);
        hotel.save();
        System.out.println("Reservation " + reservation.getBookingId() + " has been cancelled.");
        System.out.println("Room " + reservation.getRoom().getRoomNumber()
                + " is now free for those dates.");
    }

    private void viewBookingDetails() {
        String bookingId = input.readLine("Enter booking ID: ");
        hotel.findReservation(bookingId)
                .ifPresentOrElse(
                        reservation -> System.out.println(reservation.toDetailedString()),
                        () -> System.out.println("No reservation found for that booking ID.")
                );
    }

    private void makePayment() throws IOException {
        String bookingId = input.readLine("Enter booking ID: ");
        Reservation reservation = hotel.findReservation(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No reservation found for that booking ID."));
        System.out.printf("Amount payable: Rs. %.2f%n", reservation.getTotalAmount());
        System.out.println("Payment method: 1) UPI  2) Credit/Debit Card  3) Cash");
        PaymentMethod method = PaymentMethod.fromUserInput(input.readLine("Choose method: "));
        if (method == null) {
            System.out.println("Unknown payment method.");
            return;
        }

        String extra = switch (method) {
            case UPI -> input.readLine("Enter UPI ID (example: name@upi): ");
            case CARD -> input.readLine("Enter 16-digit card number (use 0000... to simulate failure): ");
            case CASH -> "Paid at reception";
        };

        Payment payment = hotel.makePayment(reservation.getBookingId(), method, extra);
        hotel.save();
        if (payment.getStatus().name().equals("PAID")) {
            System.out.println("\nPayment successful.");
            System.out.println(payment.toReceipt());
        } else {
            System.out.println("\nPayment failed. " + payment.getReceiptNote());
            System.out.println("The reservation is still confirmed. You may try again.");
        }
    }

    private void viewAllReservations() {
        List<Reservation> all = hotel.getAllReservations();
        if (all.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
        System.out.println("\n--- All Reservations ---");
        for (Reservation reservation : all) {
            System.out.println(reservation);
        }
    }

    private void exit() throws IOException {
        hotel.save();
        running = false;
        System.out.println("Thank you for visiting Sunrise Hotel. Data saved. Goodbye!");
    }
}
