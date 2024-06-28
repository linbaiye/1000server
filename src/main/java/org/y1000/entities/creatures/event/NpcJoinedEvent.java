package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.gen.Packet;

public final class NpcJoinedEvent extends Abstract2ClientEntityEvent  {
    public NpcJoinedEvent(Npc source) {
        super(source);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return source().captureInterpolation().toPacket();
    }
}
