package org.y1000.entities.objects;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.realm.DynamicObjectEventHandler;

public class DynamicObjectOpenEvent extends AbstractDynamicObjectEvent implements I2ClientMessage  {
    protected DynamicObjectOpenEvent(DynamicObject source) {
        super(source);
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {

    }

    @Override
    public Packet toPacket() {
        return null;
    }
}
