package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.message.SetPositionEvent;

public final class PassiveMonsterMoveState extends AbstractCreatureMoveState<PassiveMonster> {

    private Creature attacker;

    public PassiveMonsterMoveState(int millisPerUnit, Direction towards) {
        super(State.WALK, millisPerUnit, towards);
    }

    private void nextMove(PassiveMonster monster) {
        monster.changeState(PassiveMonsterIdleState.ofMonster(monster));
        monster.emitEvent(SetPositionEvent.fromCreature(monster));
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

    private void afterAttacked(PassiveMonster monster, Creature attacker) {
        if (this.attacker != null) {
            this.attacker = attacker;
        }
    }

    @Override
    public void attackedBy(PassiveMonster monster, Creature attacker) {
        if (!attacker.harhAttribute().randomHit(monster.harhAttribute())) {
            return;
        }
    }

    public static PassiveMonsterMoveState buffalo(Direction towards) {
        return new PassiveMonsterMoveState(1050, towards);
    }

    public static PassiveMonsterMoveState of(PassiveMonster monster, Direction towords) {
        return new PassiveMonsterMoveState(monster.getStateMillis(State.WALK), towords);
    }
}
