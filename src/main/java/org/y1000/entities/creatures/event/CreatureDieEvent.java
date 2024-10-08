package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.CreatureDieEventPacket;
import org.y1000.network.gen.Packet;

public final class CreatureDieEvent extends AbstractCreatureEvent {

    public CreatureDieEvent(Creature source) {
        super(source);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setDie(CreatureDieEventPacket.newBuilder().setId(source().id())
                        .build()).build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
