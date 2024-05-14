package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.network.gen.CreatureAttackEventPacket;
import org.y1000.network.gen.Packet;

public abstract class AbstractCreatureAttackEvent extends AbstractCreatureEvent {

    protected AbstractCreatureAttackEvent(Creature source) {
        super(source);
    }


    protected abstract CreatureAttackEventPacket.Builder setCreatureSpecificFields(
            CreatureAttackEventPacket.Builder builder);


    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setAttackEventPacket(
                        setCreatureSpecificFields(
                                CreatureAttackEventPacket.newBuilder()
                                .setDirection(creature().direction().value())
                                .setId(source().id()))
                                .build())
                .build();
    }
}
