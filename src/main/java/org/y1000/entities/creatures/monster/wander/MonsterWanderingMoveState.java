package org.y1000.entities.creatures.monster.wander;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.AbstractMonsterMoveState;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterWanderingMoveState extends AbstractMonsterMoveState implements WanderingState {
    private final Coordinate destination;

    private MonsterWanderingMoveState(AbstractMonster passiveMonster, Coordinate dest) {
        super(passiveMonster.coordinate(), passiveMonster.direction(), passiveMonster.getStateMillis(State.WALK));
        this.destination = dest;
    }


    @Override
    protected AbstractMonsterIdleState destinationBlockedState(AbstractMonster monster) {
        return MonsterWanderingIdleState.reroll(monster);
    }

    @Override
    protected void onArrived(AbstractMonster monster) {
        monster.changeState(MonsterWanderingIdleState.again(monster, destination, getStart()));
        monster.emitEvent(MonsterChangeStateEvent.of(monster));
    }

    public static MonsterWanderingMoveState move(AbstractMonster monster, Coordinate target) {
        return new MonsterWanderingMoveState(monster, target);
    }
}
