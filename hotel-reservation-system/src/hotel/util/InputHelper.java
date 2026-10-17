package hotel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Reads and validates console input so the rest of the app stays simple.
 */
public class InputHelper {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                double value = Double.parseDouble(raw);
                if (value < 0) {
                    System.out.println("Value cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String raw = readLine(prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(raw, DATE_FORMAT);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Example: 2026-12-25");
            }
        }
    }

    public String readName(String prompt) {
        while (true) {
            String name = readLine(prompt);
            if (name.length() >= 2 && name.matches("[A-Za-z ]+")) {
                return name;
            }
            System.out.println("Enter a valid name using letters and spaces only.");
        }
    }

    public String readPhone(String prompt) {
        while (true) {
            String phone = readLine(prompt);
            if (PHONE.matcher(phone).matches()) {
                return phone;
            }
            System.out.println("Enter a 10-digit phone number.");
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String email = readLine(prompt);
            if (EMAIL.matcher(email).matches()) {
                return email;
            }
            System.out.println("Enter a valid email address, for example guest@example.com");
        }
    }

    public boolean isValidStay(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            System.out.println("Check-out date must be after check-in date.");
            return false;
        }
        if (checkIn.isBefore(LocalDate.now())) {
            System.out.println("Check-in date cannot be in the past.");
            return false;
        }
        return true;
    }
}
