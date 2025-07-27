package org.y1000.entities.objects;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.realm.DynamicObjectEventHandler;

public class DynamicObjectAnimationEvent extends AbstractDynamicObjectEvent implements I2ClientMessage  {
    private final Packet packet;
    protected DynamicObjectAnimationEvent(DynamicObject source, Packet packet) {
        super(source);
        this.packet = packet;
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
