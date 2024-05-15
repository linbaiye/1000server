package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

public final class PassiveMonsterMoveState extends AbstractCreatureMoveState<PassiveMonster> implements
        MonsterState<PassiveMonster> {
    private Creature attacker;

    public PassiveMonsterMoveState(Coordinate start, Direction towards, int millisPerUnit) {
        this(start, towards, millisPerUnit, null);
    }

    public PassiveMonsterMoveState(Coordinate start, Direction towards, int millisPerUnit, Creature attacker) {
        super(State.WALK, start, towards, millisPerUnit);
        this.attacker = attacker;
    }

    @Override
    public void update(PassiveMonster monster, int delta) {
        walkMillis(monster, delta);
        if (elapsedMillis() < getTotalMillis()) {
            return;
        }
        if (!tryChangeCoordinate(monster, monster.realmMap())) {
            monster.changeCoordinate(getStart());
        }
        if (attacker != null) {
            monster.retaliate(attacker);
        } else {
            monster.changeState(PassiveMonsterIdleState.of(monster));
            monster.emitEvent(SetPositionEvent.of(monster));
        }
    }

    @Override
    public void afterAttacked(PassiveMonster monster, Creature attacker) {
        if (this.attacker != null) {
            this.attacker = attacker;
        }
    }

    public static PassiveMonsterMoveState of(PassiveMonster monster, Direction towards) {
        return new PassiveMonsterMoveState(monster.coordinate(), towards, monster.getStateMillis(State.WALK));
    }

    public static PassiveMonsterMoveState towardsAttacker(int millis, Coordinate start, Direction towards, Creature attacker) {
        return new PassiveMonsterMoveState(start, towards, millis, attacker);
    }
}
