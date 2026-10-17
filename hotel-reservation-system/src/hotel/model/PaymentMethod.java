package hotel.model;

/**
 * Simulated payment options. No real money is processed.
 */
public enum PaymentMethod {
    UPI,
    CARD,
    CASH;

    public static PaymentMethod fromUserInput(String input) {
        if (input == null) {
            return null;
        }
        String value = input.trim();
        if (value.equals("1")) {
            return UPI;
        }
        if (value.equals("2")) {
            return CARD;
        }
        if (value.equals("3")) {
            return CASH;
        }
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
