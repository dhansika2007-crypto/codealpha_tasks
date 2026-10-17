package hotel.model;

/**
 * Base class for all hotel rooms. Subclasses represent room categories
 * and provide category-specific descriptions (inheritance).
 */
public abstract class Room {
    private final int roomNumber;
    private final RoomType roomType;
    private double pricePerNight;
    private boolean available;

    protected Room(int roomNumber, RoomType roomType, double pricePerNight, boolean available) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Category-specific description implemented by subclasses.
     */
    public abstract String getDescription();

    @Override
    public String toString() {
        String status = available ? "Available" : "Occupied / Reserved";
        return String.format("Room %-4d | %-8s | Rs. %-8.2f / night | %-18s | %s",
                roomNumber, roomType.getDisplayName(), pricePerNight, status, getDescription());
    }

    /**
     * Factory used when loading rooms from storage.
     */
    public static Room create(int roomNumber, RoomType type, double price, boolean available) {
        switch (type) {
            case STANDARD:
                return new StandardRoom(roomNumber, price, available);
            case DELUXE:
                return new DeluxeRoom(roomNumber, price, available);
            case SUITE:
                return new SuiteRoom(roomNumber, price, available);
            default:
                throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }
}
