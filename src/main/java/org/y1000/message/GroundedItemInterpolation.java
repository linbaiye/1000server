package org.y1000.message;

import lombok.Builder;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowItemPacket;
import org.y1000.util.Coordinate;

public final class GroundedItemInterpolation extends AbstractEntityInterpolation {
    private final String name;
    private final Integer number;
    private Packet packet;

    @Builder
    public GroundedItemInterpolation(long id, Coordinate coordinate, String name, Integer number) {
        super(id, coordinate);
        this.name = name;
        this.number = number;
    }

    @Override
    public Packet toPacket() {
        if (packet != null) {
            return packet;
        }
        ShowItemPacket.Builder showItemBuidler = ShowItemPacket.newBuilder()
                .setCoordinateX(coordinate().x())
                .setCoordinateY(coordinate().y())
                .setName(name)
                .setId(getId());
        if (number != null) {
            showItemBuidler.setNumber(number);
        }
        packet = Packet.newBuilder().setShowItem(showItemBuidler).build();
        return packet;
    }
}
