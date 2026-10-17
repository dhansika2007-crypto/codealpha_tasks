package hotel.model;

/**
 * Deluxe category room.
 */
public class DeluxeRoom extends Room {
    public DeluxeRoom(int roomNumber, double pricePerNight, boolean available) {
        super(roomNumber, RoomType.DELUXE, pricePerNight, available);
    }

    @Override
    public String getDescription() {
        return RoomType.DELUXE.getDescription();
    }
}
