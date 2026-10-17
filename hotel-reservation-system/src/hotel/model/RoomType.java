package hotel.model;

/**
 * Categories of rooms offered by the hotel.
 */
public enum RoomType {
    STANDARD("Standard", "Comfortable room with a queen bed, Wi-Fi, and attached bath."),
    DELUXE("Deluxe", "Spacious room with a king bed, city view, mini fridge, and workspace."),
    SUITE("Suite", "Luxury suite with living area, premium amenities, and complimentary breakfast.");

    private final String displayName;
    private final String description;

    RoomType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Parses user input such as "1", "standard", or "STANDARD".
     */
    public static RoomType fromUserInput(String input) {
        if (input == null) {
            return null;
        }
        String value = input.trim();
        if (value.equals("1")) {
            return STANDARD;
        }
        if (value.equals("2")) {
            return DELUXE;
        }
        if (value.equals("3")) {
            return SUITE;
        }
        try {
            return RoomType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
