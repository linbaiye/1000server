package org.y1000.entities.creatures.monster;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterEscapeAI extends AbstractMonsterFightAI {
    @Override
    public void onMoveDone(AbstractMonster monster) {

    }

    @Override
    public void onMoveFailed(AbstractMonster monster) {

    }

    @Override
    public void onIdleDone(AbstractMonster monster) {

    }

    @Override
    public void onFrozenDone(AbstractMonster monster) {

    }

    @Override
    public void onHurtDone(AbstractMonster monster) {

    }

    @Override
    public void start(AbstractMonster monster) {

    }
//
//    private long elapsed;
//
//    private final MonsterAI after;
//
//    private Coordinate dest;
//
//    private Coordinate previous;
//
//    public MonsterEscapeAI(MonsterAI after) {
//        this.after = after;
//    }
//
//    @Override
//    public void onMoveDone(AbstractMonster monster) {
//        previous = monster.coordinate();
//        if (monster.coordinate().equals(dest)) {
//            monster.changeAI(after);
//        } else {
//            doMove(monster);
//        }
//    }
//
//
//    private void doMove(AbstractMonster monster) {
//        moveProcess(monster, dest, previous, () -> monster.changeAI(after), monster.runSpeed() / 2);
//    }
//
//    @Override
//    public void onMoveFailed(AbstractMonster monster) {
//        monster.changeAI(after);
//    }
//
//    @Override
//    public void onIdleDone(AbstractMonster monster) {
//        doMove(monster);
//    }
//
//    @Override
//    public void onFrozenDone(AbstractMonster monster) {
//        doMove(monster);
//    }
//
//
//    private Coordinate computeCoordinate(AbstractMonster monster) {
//        Direction direction = monster.getFightingEntity().coordinate().computeDirection(monster.coordinate());
//        Coordinate[] targets = new Coordinate[4];
//        targets[0] = monster.coordinate().moveBy(direction) ;
//        for (int i = 1; i < 4; i++) {
//            targets[i]  = targets[i-1].moveBy(direction);
//        }
//        for (int i = 3; i >= 0; i--) {
//            if (monster.realmMap().movable(targets[i])) {
//                return targets[i];
//            }
//        }
//        return null;
//    }
//
//
//    @Override
//    public void start(AbstractMonster monster) {
//        elapsed = 20000;
//        dest = computeCoordinate(monster);
//        previous = monster.coordinate();
//        log.debug("Changed to escape ai, escape to coordinate {}", dest);
//        if (dest == null) {
//            monster.changeAI(after);
//        } else {
//            doMove(monster);
//        }
//    }
//
//
//    @Override
//    public void update(AbstractMonster monster, long delta) {
//        elapsed -= delta;
//        if (elapsed <= 0) {
//            monster.changeAI(after);
//        }
//    }
}
