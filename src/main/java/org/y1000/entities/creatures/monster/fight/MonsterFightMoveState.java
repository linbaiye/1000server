package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.AbstractMonsterMoveState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

public final class MonsterFightMoveState extends AbstractMonsterMoveState implements MonsterFightState {
    public MonsterFightMoveState(Coordinate start, Direction towards, int millisPerUnit) {
        super(start, towards, millisPerUnit);
    }

    @Override
    protected AbstractMonsterIdleState destinationBlockedState(AbstractMonster monster) {
        return MonsterFightIdleState.start(monster);
    }

    @Override
    protected void onArrived(AbstractMonster monster) {
        attackIfAdjacentOrNextMove(monster, () -> continueMove(monster));
    }

    public static MonsterFightMoveState towardsCurrentDirection(AbstractMonster monster) {
        return new MonsterFightMoveState(monster.coordinate(), monster.direction(), monster.getStateMillis(State.WALK));
    }

    private void continueMove(AbstractMonster creature) {
        creature.changeState(MonsterFightIdleState.nextStep(creature, getStart()));
        creature.emitEvent(CreatureChangeStateEvent.of(creature));
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
        attackIfAdjacentOrNextMove(creature, () -> continueMove(creature));
    }
}
