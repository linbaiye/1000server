package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterMeleeFightAI extends AbstractMonsterFightAI {

    private Coordinate previousCoordinate;


    private boolean targetCloseEnough(AbstractMonster monster) {
        return monster.getFightingEntity().attackable() &&
                monster.getFightingEntity().coordinate().directDistance(monster.coordinate()) <= 1;
    }

    @Override
    public void onMoveDone(AbstractMonster monster) {
        previousCoordinate = monster.coordinate();
        process(monster, () -> toIdle(monster));
    }

    @Override
    public void onMoveFailed(AbstractMonster monster) {
        process(monster, () -> toIdle(monster));
    }

    @Override
    public void onIdleDone(AbstractMonster monster) {
        process(monster, () -> changeToNewState(monster, MonsterCommonState.freeze(monster)));
    }

    @Override
    public void onFrozenDone(AbstractMonster monster) {
        process(monster, () -> moveCloser(monster));
    }

    @Override
    public void onAttackDone(AbstractMonster monster) {
        process(monster, () -> moveCloser(monster));
    }

    private void moveCloser(AbstractMonster monster) {
        moveProcess(monster, monster.getFightingEntity().coordinate(), previousCoordinate, () -> toIdle(monster));
    }


    private void process(AbstractMonster monster, Action monsterFarAction) {
        if (monster.getFightingEntity() == null) {
            monster.changeAI(MonsterWanderingAI.create(monster));
            return;
        }
        if (!targetCloseEnough(monster)) {
            monsterFarAction.invoke();
            return;
        }
        attackProcess(monster, () -> doMeleeAttack(monster));
    }



    private void toIdle(AbstractMonster monster) {
        changeToNewState(monster, MonsterCommonState.idle(monster));
    }

    @Override
    public void start(AbstractMonster monster) {
        previousCoordinate = monster.coordinate();
        process(monster, () -> toIdle(monster));
    }
}
