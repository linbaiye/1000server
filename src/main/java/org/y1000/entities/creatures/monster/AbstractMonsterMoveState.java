package org.y1000.entities.creatures.monster;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterMoveState extends AbstractCreatureMoveState<PassiveMonster> implements
        MonsterState<PassiveMonster> {

    public AbstractMonsterMoveState(Coordinate start, Direction towards, int millisPerUnit) {
        super(State.WALK, start, towards, millisPerUnit);
    }

    protected abstract AbstractMonsterIdleState destinationBlockedState(PassiveMonster monster);

    protected abstract void onArrived(PassiveMonster monster);

    @Override
    public void update(PassiveMonster monster, int delta) {
        if (!elapse(delta)) {
            return;
        }
        if (tryChangeCoordinate(monster, monster.realmMap())) {
            onArrived(monster);
        } else {
            monster.changeState(destinationBlockedState(monster));
            monster.emitEvent(SetPositionEvent.of(monster));
        }
    }

    @Override
    public void moveToHurtCoordinate(PassiveMonster creature) {
        tryChangeCoordinate(creature, creature.realmMap());
    }
}
