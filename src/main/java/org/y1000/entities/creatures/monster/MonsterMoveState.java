package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterMoveState extends AbstractCreatureMoveState<AbstractMonster> implements
        MonsterState<AbstractMonster> {

    private MonsterMoveState(Coordinate start,
                            Direction towards,
                            int millisPerUnit) {
        super(State.WALK, start, towards, millisPerUnit);
    }

    @Override
    public void update(AbstractMonster monster, int delta) {
        if (elapsedMillis() == 0) {
            monster.realmMap().free(monster);
        }
        if (!elapse(delta)) {
            return;
        }
        if (tryChangeCoordinate(monster, monster.realmMap())) {
//            monster.AI().onMoveDone(monster);
        } else {
//            monster.AI().onMoveFailed(monster);
        }
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
//        creature.AI().onMoveDone(creature);
    }

    @Override
    public void moveToHurtCoordinate(AbstractMonster creature) {
        tryChangeCoordinate(creature, creature.realmMap());
    }

    public static MonsterMoveState move(AbstractMonster monster, int speed) {
        return new MonsterMoveState(monster.coordinate(), monster.direction(), speed);
    }
}
