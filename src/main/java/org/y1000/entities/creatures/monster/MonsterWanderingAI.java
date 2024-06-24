package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;

public final class MonsterWanderingAI extends AbstractMonsterAI {

    private Coordinate destination;

    private Coordinate previousCoordinate;

    private long timeLeftToSound;


    public MonsterWanderingAI(Coordinate destination,
                              Coordinate previousCoordinate) {
        this.destination = destination;
        this.previousCoordinate = previousCoordinate;
    }

    @Override
    public void onMoveDone(AbstractMonster monster) {
        previousCoordinate = monster.coordinate();
        if (monster.coordinate().equals(destination)) {
            nextRound(monster);
        } else {
            changeToNewState(monster, MonsterCommonState.idle(monster));
        }
    }

    @Override
    public void onMoveFailed(AbstractMonster monster) {
        monster.emitEvent(SetPositionEvent.of(monster));
        nextRound(monster);
    }

    @Override
    public void onIdleDone(AbstractMonster monster) {
        changeToNewState(monster, MonsterCommonState.freeze(monster));
    }

    @Override
    public void onFrozenDone(AbstractMonster monster) {
        moveProcess(monster, destination, previousCoordinate, () -> nextRound(monster));
    }

    @Override
    public void onHurtDone(AbstractMonster monster) {
        if (monster.attackSkill() instanceof MonsterRangedAttackSkill skill) {
            monster.changeAI(new MonsterRangedFightAI(skill));
        } else {
            monster.changeAI(new MonsterMeleeFightAI());
        }
    }


    private void nextRound(AbstractMonster monster) {
        destination = monster.wanderingArea().random(monster.getSpwanCoordinate());
        previousCoordinate = monster.coordinate();
        changeToNewState(monster, MonsterCommonState.idle(monster));
    }

    @Override
    public void start(AbstractMonster monster) {
        changeToNewState(monster, MonsterCommonState.idle(monster));
        setSoundTimer();
    }

    private void setSoundTimer() {
        timeLeftToSound = ThreadLocalRandom.current().nextInt(15, 25) * 1000L;
    }
    @Override
    public void update(AbstractMonster monster, long delta) {
        if (monster.normalSound().isEmpty()) {
            return;
        }
        timeLeftToSound = Math.max(0, timeLeftToSound - delta);
        if (timeLeftToSound == 0) {
            setSoundTimer();
            monster.emitEvent(new CreatureSoundEvent(monster, monster.normalSound().orElse("")));
        }
    }

    public static MonsterWanderingAI create(AbstractMonster monster) {
        return new MonsterWanderingAI(monster.wanderingArea().random(monster.getSpwanCoordinate()), monster.coordinate());
    }
}
