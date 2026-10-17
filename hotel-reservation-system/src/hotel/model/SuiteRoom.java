package hotel.model;

/**
 * Suite category room.
 */
public class SuiteRoom extends Room {
    public SuiteRoom(int roomNumber, double pricePerNight, boolean available) {
        super(roomNumber, RoomType.SUITE, pricePerNight, available);
    }

    @Override
    public String getDescription() {
        return RoomType.SUITE.getDescription();
    }
}
