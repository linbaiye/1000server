package org.y1000.entities;

import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RemoveEntityPacket;

public class EntityLeftRealmEvent extends Abstract2ClientEntityEvent {

    public EntityLeftRealmEvent(PhysicalEntity source) {
        super(source);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setRemoveEntity(RemoveEntityPacket.newBuilder().setId(source().id()).build())
                .build();
    }
}
