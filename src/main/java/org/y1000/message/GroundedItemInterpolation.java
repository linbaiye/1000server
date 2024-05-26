package org.y1000.message;

import lombok.Builder;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowItemPacket;
import org.y1000.util.Coordinate;

public class GroundedItemInterpolation extends AbstractEntityInterpolation {
    private final String name;
    private final int x;
    private final int y;
    private final Integer number;
    private Packet packet;

    @Builder
    public GroundedItemInterpolation(long id, Coordinate coordinate, String name, int x, int y, Integer number) {
        super(id, coordinate);
        this.name = name;
        this.x = x;
        this.y = y;
        this.number = number;
    }

    @Override
    public Packet toPacket() {
        if (packet != null) {
            return packet;
        }
        ShowItemPacket.Builder showItemBuidler = ShowItemPacket.newBuilder()
                .setX(x)
                .setY(y)
                .setCoordinateX(coordinate().x())
                .setCoordinateY(coordinate().y())
                .setName(name)
                .setId((int) getId());
        if (number != null) {
            showItemBuidler.setNumber(number);
        }
        packet = Packet.newBuilder().setShowItem(showItemBuidler).build();
        return packet;
    }
}
