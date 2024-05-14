package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.message.SetPositionEvent;

public final class PassiveMonsterMoveState extends AbstractCreatureMoveState<PassiveMonster> {

    private Creature attacker;

    public PassiveMonsterMoveState(int millisPerUnit, Direction towards) {
        super(State.WALK, millisPerUnit, towards);
    }

    public PassiveMonsterMoveState(int millisPerUnit, Direction towards, Creature attacker) {
        super(State.WALK, millisPerUnit, towards);
        this.attacker = attacker;
    }

    private void nextMove(PassiveMonster monster) {
        if (attacker != null) {
            monster.retaliate(attacker);
        } else {
            monster.changeState(PassiveMonsterIdleState.ofMonster(monster));
            monster.emitEvent(SetPositionEvent.ofCreature(monster));
        }
    }

    @Override
    public void update(PassiveMonster monster, int delta) {
        walkMillis(monster, delta);
        if (elapsedMillis() < millisPerUnit()) {
            return;
        }
        tryChangeCoordinate(monster, monster.realmMap());
        nextMove(monster);
    }

    @Override
    public void afterAttacked(PassiveMonster monster, Creature attacker) {
        if (this.attacker != null) {
            this.attacker = attacker;
        }
    }

    public static PassiveMonsterMoveState of(PassiveMonster monster, Direction towords) {
        return new PassiveMonsterMoveState(monster.getStateMillis(State.WALK), towords);
    }
    public static PassiveMonsterMoveState towardsAttacker(int millis, Direction towords, Creature attacker) {
        return new PassiveMonsterMoveState(millis, towords, attacker);
    }
}
