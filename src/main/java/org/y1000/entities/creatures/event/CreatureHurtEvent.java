package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.CreatureHurtEventPacket;
import org.y1000.network.gen.Packet;

public final class CreatureHurtEvent extends AbstractCreatureEvent {

    public CreatureHurtEvent(Creature source) {
        super(source);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setHurtEventPacket(CreatureHurtEventPacket
                        .newBuilder()
                        .setId(source().id())
                        .build())
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
