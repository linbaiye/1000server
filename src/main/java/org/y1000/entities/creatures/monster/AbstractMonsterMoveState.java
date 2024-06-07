package org.y1000.entities.creatures.monster;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterMoveState extends AbstractCreatureMoveState<AbstractMonster> implements
        MonsterState<AbstractMonster> {

    public AbstractMonsterMoveState(Coordinate start, Direction towards, int millisPerUnit) {
        super(State.WALK, start, towards, millisPerUnit);
    }

    protected abstract AbstractMonsterIdleState destinationBlockedState(AbstractMonster monster);

    protected abstract void onArrived(AbstractMonster monster);

    @Override
    public void update(AbstractMonster monster, int delta) {
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
    public void moveToHurtCoordinate(AbstractMonster creature) {
        tryChangeCoordinate(creature, creature.realmMap());
    }
}
