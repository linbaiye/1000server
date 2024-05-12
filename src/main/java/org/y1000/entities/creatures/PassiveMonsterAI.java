package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Rectangle;

import java.util.concurrent.ThreadLocalRandom;

final class PassiveMonsterAI {

    private final PassiveMonster monster;

    private final Rectangle wanderingArea;

    private final ThreadLocalRandom random;

    private Behaviour behaviour;

    private interface Behaviour {

        void behave(PassiveMonster monster);

    }

    private class IdleBehaviour implements Behaviour {
        private int count = -1;
        private void turn() {
            Direction[] values = monster.direction().neighbours();
            var index = random.nextInt(0, values.length);
            Direction direction = values[index];
            monster.changeDirection(direction);
            monster.changeState(PassiveMonsterIdleState.ofMonster(monster));
            monster.emitEvent(MoveEvent.setPosition(monster));
        }

        private void moveOrTurn( ) {
            Direction towards = monster.direction();
            var next = monster.coordinate().moveBy(towards);
            if (wanderingArea.contains(next) && monster.realmMap().movable(next)) {
                monster.changeState(PassiveMonsterMoveState.buffalo(towards));
                monster.emitEvent(MoveEvent.movingTo(monster, towards));
                behaviour = new MoveBehaviour();
            } else {
                turn();
            }
        }

        @Override
        public void behave(PassiveMonster monster) {
            if (count >= 5) {
                moveOrTurn();
                count = 0;
                return;
            }
            count++;
            var nextInt = random.nextInt(0, 2);
            if (nextInt == 0) {
                monster.changeState(PassiveMonsterIdleState.ofMonster(monster));
            } else {
                turn();
            }
        }
    }

    private class MoveBehaviour implements Behaviour {

        private int count = 0;

        @Override
        public void behave(PassiveMonster monster) {
            if (count++ == 0) {
                monster.changeState(PassiveMonsterIdleState.ofMonster(monster));
                monster.emitEvent(MoveEvent.setPosition(monster));
            }
            if (count > 1) {
                if ((random.nextInt() & 1) != 0) {
                    behaviour = new IdleBehaviour();
                }
            }
        }
    }

    private class AttackBehaviour implements Behaviour {

        private final Creature target;

        private AttackBehaviour(Creature target) {
            this.target = target;
        }

        @Override
        public void behave(PassiveMonster monster) {
        }
    }

    private class FightAi implements Behaviour {
        private final Creature target;

        private FightAi(Creature target) {
            this.target = target;
        }

        private void behaveAfterHurt(PassiveMonsterHurtState hurtState) {

        }

        @Override
        public void behave(PassiveMonster monster) {
            if (!(monster.state() instanceof PassiveMonsterHurtState hurtState)) {
                return;
            }
            if (monster.coordinate().moveBy(monster.direction()).equals(target.coordinate())) {
                monster.changeDirection(monster.coordinate().computeDirection(target.coordinate()));
                if (monster.recoveryCooldown() > 0) {
                    monster.changeState(PassiveMonsterIdleState.recovery(monster.recoveryCooldown()));
                    monster.emitEvent(SetPositionEvent.fromCreature(monster));
                } else {
                }
            } else {
            }
        }
    }


    PassiveMonsterAI(PassiveMonster monster) {
        this.monster = monster;
        this.wanderingArea = new Rectangle(monster.coordinate().move(-10, -10),
                monster.coordinate().move(10, 10));
        random = ThreadLocalRandom.current();
        behaviour = new IdleBehaviour();
    }

    public void getAttacked(Creature attacker) {
        if (!monster.harhAttribute().randomHit(attacker.harhAttribute())) {
            return;
        }
        behaviour = new FightAi(attacker);
    }


    public void nextMove() {
        behaviour.behave(monster);
    }
}
