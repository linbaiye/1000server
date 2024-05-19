package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.AbstractMonsterMoveState;
import org.y1000.util.Coordinate;

public final class MonsterFightingMoveState extends AbstractMonsterMoveState
        implements MonsterFightingState {
    private final Creature target;

    private MonsterFightingMoveState(Coordinate start, Direction towards, int millisPerUnit, Creature target) {
        super(start, towards, millisPerUnit);
        this.target = target;
    }

    @Override
    protected AbstractMonsterIdleState destinationBlockedState(PassiveMonster monster) {
        return MonsterFightingIdleState.hunt(monster, target);
    }

    @Override
    protected void onArrived(PassiveMonster monster) {
        if (monster.coordinate().directDistance(target.coordinate()) <= 1) {
            monster.attack(target);
        } else {
            monster.changeState(MonsterFightingIdleState.continueHunting(monster, target, getStart()));
            monster.emitEvent(ChangeStateEvent.of(monster));
        }
    }

    public static MonsterFightingMoveState move(PassiveMonster monster, Creature target) {
        return new MonsterFightingMoveState(monster.coordinate(), monster.direction(), monster.getStateMillis(State.WALK), target);
    }

    @Override
    public Creature currentTarget() {
        return target;
    }
}
