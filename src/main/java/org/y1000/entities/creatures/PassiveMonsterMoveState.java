package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

@Slf4j
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
        if (!walkMillis(monster, delta)) {
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
    public void afterHurt(PassiveMonster monster, Creature attacker) {
        if (this.attacker != null) {
            log.debug("Attacked, record attacker.");
            this.attacker = attacker;
        }
        log.debug("Back to move.");
        monster.changeState(this);
    }

    public static PassiveMonsterMoveState of(PassiveMonster monster, Direction towards) {
        return new PassiveMonsterMoveState(monster.coordinate(), towards, monster.getStateMillis(State.WALK));
    }

    public static PassiveMonsterMoveState towardsAttacker(int millis, Coordinate start, Direction towards, Creature attacker) {
        return new PassiveMonsterMoveState(start, towards, millis, attacker);
    }
}
