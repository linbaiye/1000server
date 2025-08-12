package org.y1000.entities;

import lombok.Builder;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowItemPacket;
import org.y1000.util.Coordinate;

public final class GroundItemSnapshot implements I2ClientMessage {
    private final String name;
    private final Integer number;
    private Packet packet;

    private final int color;

    private final int icon;
    private final long id;
    private final Coordinate coordinate;


    @Builder
    public GroundItemSnapshot(long id, Coordinate coordinate, String name, Integer number, int color, int icon) {
        this.id = id;
        this.coordinate = coordinate;
        this.name = name;
        this.number = number;
        this.color = color;
        this.icon = icon;
    }

    @Override
    public Packet toPacket() {
        if (packet != null) {
            return packet;
        }
        ShowItemPacket.Builder showItemBuidler = ShowItemPacket.newBuilder()
                .setCoordinateX(coordinate.x())
                .setCoordinateY(coordinate.y())
                .setName(name)
                .setColor(color)
                .setIcon(icon)
                .setId(id);
        if (number != null) {
            showItemBuidler.setNumber(number);
        }
        packet = Packet.newBuilder().setShowItem(showItemBuidler).build();
        return packet;
    }
}
