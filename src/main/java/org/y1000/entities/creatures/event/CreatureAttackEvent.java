package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;
import org.y1000.network.gen.Packet;

public final class CreatureAttackEvent extends AbstractCreatureEvent {

    private final State attackState;

    public CreatureAttackEvent(Creature source, State state) {
        super(source);
        attackState = state;
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
                                .setState(attackState.value())
                                .setPlayer(false)
                                .setId(source().id())
                                .build())
                .build();
    }

    public static CreatureAttackEvent ofMonster(PassiveMonster monster) {
        return new CreatureAttackEvent(monster, State.ATTACK);
    }
}
