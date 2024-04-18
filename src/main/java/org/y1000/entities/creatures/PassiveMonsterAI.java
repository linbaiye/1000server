package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.message.MoveEvent;
import org.y1000.util.Rectangle;

import java.util.concurrent.ThreadLocalRandom;

final class PassiveMonsterAI {

    private final PassiveMonster monster;

    private final Rectangle wanderingArea;

    private final ThreadLocalRandom random;

    PassiveMonsterAI(PassiveMonster monster) {
        this.monster = monster;
        this.wanderingArea = new Rectangle(monster.coordinate().move(-10, -10),
                monster.coordinate().move(10, 10));
        random = ThreadLocalRandom.current();
    }

    private State randState() {
        if (random.nextInt() % 2 == 1) {
            return State.IDLE;
        } else {
            return State.WALK;
        }
    }

    private void walkOrTurn(Direction direction) {
        if (monster.direction() == direction) {
            monster.changeState(PassiveMonsterMoveState.buffalo(direction));
            monster.emitEvent(MoveEvent.movingTo(monster, direction));
        } else {
            monster.changeDirection(direction);
            monster.emitEvent(MoveEvent.setPosition(monster));
        }
    }

    private void walk() {
        monster.coordinate().neighbours().stream()
                .filter(wanderingArea::contains)
                .filter(monster.realmMap()::movable)
                .findFirst()
                .flatMap(monster.coordinate()::computeDirection)
                .ifPresent(this::walkOrTurn);
    }

    private void turn() {
        if (random.nextInt() % 2 == 1) {
            monster.changeState(PassiveMonsterIdleState.buffalo());
            return;
        }
        Direction[] values = monster.direction().neighbours();
        var index = random.nextInt(0, values.length);
        Direction direction = values[index];
        monster.changeDirection(direction);
        monster.changeState(PassiveMonsterIdleState.buffalo());
        monster.emitEvent(MoveEvent.setPosition(monster));
    }

    void nextMove() {
//        if (monster.state().stateEnum() == State.WALK) {
            turn();
            return;
//        }
//        var state = randState();
//        if (state == State.WALK) {
//            walk();
//        } else {
//            turn();
//        }
    }
}
