package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.AbstractMonsterMoveState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

public class MonsterFightMoveState extends AbstractMonsterMoveState {
    public MonsterFightMoveState(Coordinate start, Direction towards, int millisPerUnit) {
        super(start, towards, millisPerUnit);
    }

    @Override
    protected AbstractMonsterIdleState destinationBlockedState(PassiveMonster monster) {
        return MonsterFightIdleState.start(monster);
    }

    @Override
    protected void onArrived(PassiveMonster monster) {
        if (monster.getFightingEntity() == null) {
            monster.nextHuntMove();
        } else {
            monster.changeState(MonsterFightIdleState.nextStep(monster, getStart()));
            monster.emitEvent(CreatureChangeStateEvent.of(monster));
        }
    }

    public static MonsterFightMoveState towardsCurrentDirection(PassiveMonster monster) {
        return new MonsterFightMoveState(monster.coordinate(), monster.direction(), monster.getStateMillis(State.WALK));
    }
}
