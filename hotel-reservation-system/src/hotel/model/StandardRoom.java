package hotel.model;

/**
 * Standard category room.
 */
public class StandardRoom extends Room {
    public StandardRoom(int roomNumber, double pricePerNight, boolean available) {
        super(roomNumber, RoomType.STANDARD, pricePerNight, available);
    }

    @Override
    public String getDescription() {
        return RoomType.STANDARD.getDescription();
    }
}
