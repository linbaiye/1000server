package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.Packet;

public final class CreatureDieEvent extends AbstractCreatureEvent {

    public CreatureDieEvent(Creature source) {
        super(source);
    }

    @Override
    protected Packet buildPacket() {
        return null;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
