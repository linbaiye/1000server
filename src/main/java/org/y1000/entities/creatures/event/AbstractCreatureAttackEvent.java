package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.network.gen.CreatureAttackEventPacket;
import org.y1000.network.gen.Packet;

public abstract class AbstractCreatureAttackEvent extends AbstractCreatureEvent {

    private final int millisPerSprite;

    protected AbstractCreatureAttackEvent(Creature source, int millisPerSprite) {
        super(source);
        this.millisPerSprite = millisPerSprite;
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
                                .setSpriteMillis(getMillisPerSprite())
                                .setId(source().id()))
                                .build())
                .build();
    }


    protected int getMillisPerSprite() {
        return millisPerSprite;
    }
}
