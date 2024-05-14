package org.y1000.entities.creatures;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.MoveEvent;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class PassiveMonsterIdleState extends AbstractMonsterState {

    private int idleCounter = -1;

    public PassiveMonsterIdleState(int length) {
        super(length, State.IDLE);
    }

    private PassiveMonsterIdleState(int length, int idleCounter) {
        this(length);
        this.idleCounter = idleCounter;
    }

    private void moveOrTurn(PassiveMonster monster) {
        Direction towards = monster.direction();
        var next = monster.coordinate().moveBy(towards);
        log.debug("Wandering {}, movable {}.", monster.wanderingArea().contains(next), monster.realmMap().movable(next));
        if (monster.wanderingArea().contains(next) && monster.realmMap().movable(next)) {
            monster.changeState(PassiveMonsterMoveState.of(monster, towards));
            monster.emitEvent(MoveEvent.movingTo(monster, towards));
        } else {
            turn(monster);
        }
    }

    private void turn(PassiveMonster monster) {
        Direction[] values = monster.direction().neighbours();
        var index = ThreadLocalRandom.current().nextInt(0, values.length);
        Direction direction = values[index];
        monster.changeDirection(direction);
        monster.changeState(new PassiveMonsterIdleState(monster.getStateMillis(State.IDLE), idleCounter));
        monster.emitEvent(MoveEvent.setPosition(monster));
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        if (idleCounter >= 4) {
            moveOrTurn(monster);
            idleCounter = -1;
            return;
        }
        idleCounter++;
        var nextInt = ThreadLocalRandom.current().nextInt(0, 2);
        if (nextInt == 0) {
            monster.changeState(new PassiveMonsterIdleState(monster.getStateMillis(State.IDLE), idleCounter));
        } else {
            turn(monster);
        }
    }


    @Override
    public void afterAttacked(PassiveMonster monster, Creature attacker) {
        monster.retaliate(attacker);
    }


    public static PassiveMonsterIdleState ofMonster(PassiveMonster monster) {
        return new PassiveMonsterIdleState(monster.getStateMillis(State.IDLE));
    }

    public static PassiveMonsterIdleState recovery(int len) {
        return new PassiveMonsterIdleState(len);
    }
}
