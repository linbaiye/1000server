package org.y1000.entities.creatures.monster.wander;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.AbstractMonsterMoveState;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterWanderingMoveState extends AbstractMonsterMoveState implements MonsterWanderingState {
    private final Coordinate destination;

    private MonsterWanderingMoveState(PassiveMonster passiveMonster, Coordinate dest) {
        super(passiveMonster.coordinate(), passiveMonster.direction(), passiveMonster.getStateMillis(State.WALK));
        this.destination = dest;
    }


    @Override
    protected AbstractMonsterIdleState destinationBlockedState(PassiveMonster monster) {
        return MonsterWanderingIdleState.reroll(monster);
    }

    @Override
    protected void onArrived(PassiveMonster monster) {
        monster.changeState(MonsterWanderingIdleState.again(monster, destination, getStart()));
        monster.emitEvent(ChangeStateEvent.of(monster));
    }

    public static MonsterWanderingMoveState move(PassiveMonster monster, Coordinate target) {
        return new MonsterWanderingMoveState(monster, target);
    }
}
