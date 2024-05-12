package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;
import org.y1000.network.gen.Packet;

public final class CreatureAttackEvent extends AbstractCreatureEvent {

    private final boolean below50;

    private final int millis;

    public CreatureAttackEvent(Creature source, boolean below50, int millis) {
        super(source);
        this.below50 = below50;
        this.millis = millis;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setAttackEventPacket(
                        CreatureAttackEventPacket.newBuilder()
                                .setDirection(creature().direction().value())
                                .setBelow50(below50)
                                .setPlayer(false)
                                .setSpriteMillis(millis)
                                .setId(source().id())
                                .build())
                .build();
    }

    public static CreatureAttackEvent ofMonster(PassiveMonster monster) {
        return new CreatureAttackEvent(monster, false, 0);
    }
}
