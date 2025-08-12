package org.y1000.entities.objects;

import org.y1000.entities.npc.event.NpcLifeBarEvent;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.realm.DynamicObjectEventHandler;

public class DynamicObjectLifeBarEvent extends AbstractDynamicObjectEvent implements I2ClientMessage  {

    private final Packet packet;

    protected DynamicObjectLifeBarEvent(DynamicObject source, Packet packet) {
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

    public static DynamicObjectLifeBarEvent of(DynamicObject source, int cur, int max) {
        var packet = NpcLifeBarEvent.damagedPacket(source.id(), cur, max);
        return new DynamicObjectLifeBarEvent(source, packet);
    }
}
