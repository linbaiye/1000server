package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.State;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;

public final class CreatureAttackEvent extends AbstractCreatureAttackEvent {

    public CreatureAttackEvent(Creature source) {
        super(source, source.coordinate(), source.direction());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected CreatureAttackEventPacket.Builder setCreatureSpecificFields(CreatureAttackEventPacket.Builder builder) {
        return builder.setState(State.ATTACK.value()).setPlayer(false);
    }

    public static CreatureAttackEvent ofMonster(AbstractMonster monster) {
        return new CreatureAttackEvent(monster);
    }
}
