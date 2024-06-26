package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.monster.Monster;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.gen.Packet;

public final class MonsterJoinedEvent extends Abstract2ClientEntityEvent  {
    public MonsterJoinedEvent(Monster source) {
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
