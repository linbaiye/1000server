package org.y1000.entities.creatures.monster.wander;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterWanderingIdleState extends AbstractMonsterIdleState implements MonsterWanderingState {

    private final Coordinate destination;

    private MonsterWanderingIdleState(int totalMillis, Coordinate target) {
        this(totalMillis, target, new Coordinate(0, 0));
    }

    private MonsterWanderingIdleState(int totalMillis, Coordinate target, Coordinate from) {
        super(totalMillis, from);
        this.destination = target;
    }

    public static MonsterWanderingIdleState again(PassiveMonster monster, Coordinate destination, Coordinate from) {
        return new MonsterWanderingIdleState(monster.getStateMillis(State.IDLE), destination, from);
    }

    public static MonsterWanderingIdleState start(int millis, Coordinate dest) {
        return new MonsterWanderingIdleState(millis, dest);
    }

    public static MonsterWanderingIdleState reroll(PassiveMonster monster) {
        Coordinate random = monster.wanderingArea().random(monster.coordinate());
        return new MonsterWanderingIdleState(monster.getStateMillis(State.IDLE), random);
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.changeState(MonsterWanderingFrozenState.Freeze(monster, destination, getFrom()));
        monster.emitEvent(ChangeStateEvent.of(monster));
    }
}
