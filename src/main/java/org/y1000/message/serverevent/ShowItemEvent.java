package org.y1000.message.serverevent;

import org.y1000.entities.GroundedItem;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowItemPacket;

public class ShowItemEvent extends Abstract2ClientEntityEvent {
    public ShowItemEvent(GroundedItem item) {
        super(item);
    }

    public GroundedItem item() {
        return (GroundedItem) source();
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setShowItem(ShowItemPacket.newBuilder()
                        .setId((int)item().id())
                        .setCoordinateX(item().coordinate().x())
                        .setCoordinateY(item().coordinate().y())
                        .setNumber(item().getNumber())
                        .setName(item().getName())
                        .build())
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
