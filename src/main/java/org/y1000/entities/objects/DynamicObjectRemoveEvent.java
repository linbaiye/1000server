package org.y1000.entities.objects;

import lombok.Getter;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RemoveEntityPacket;
import org.y1000.realm.DynamicObjectEventHandler;

public class DynamicObjectRemoveEvent extends AbstractDynamicObjectEvent implements I2ClientMessage  {
    private final Packet packet;

    @Getter
    private final int respawnMillis;

    protected DynamicObjectRemoveEvent(DynamicObject source, Packet packet, int respawnMillis) {
        super(source);
        this.packet = packet;
        this.respawnMillis = respawnMillis;
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        handler.onRemove( this);
    }

    @Override
    public Packet toPacket() {
        return packet;
    }

    public static DynamicObjectRemoveEvent of(DynamicObject source) {
        return DynamicObjectRemoveEvent.of(source, 0);
    }

    public static DynamicObjectRemoveEvent of(DynamicObject source, int respawnMillis) {
        RemoveEntityPacket build = RemoveEntityPacket.newBuilder().setId(source.id()).build();
        return new DynamicObjectRemoveEvent(source, Packet.newBuilder().setRemoveEntity(build).build(), respawnMillis);
    }
}
