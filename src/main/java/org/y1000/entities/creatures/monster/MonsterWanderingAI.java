package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcAI;
import org.y1000.entities.creatures.npc.ViolentNpcWanderingAI;

import java.util.concurrent.ThreadLocalRandom;


public final class MonsterWanderingAI implements NpcAI {

    private final ViolentNpcWanderingAI wrappedAi;

    private int counter;

    private void resetCounter() {
        counter = ThreadLocalRandom.current().nextInt(20, 30);
    }

    public MonsterWanderingAI(ViolentNpcWanderingAI wrappedAi) {
        this.wrappedAi = wrappedAi;
        resetCounter();
    }


    @Override
    public void onActionDone(Npc npc) {
        wrappedAi.onActionDone(npc);
        if (--counter <= 0 && npc instanceof Monster monster) {
            resetCounter();
            monster.normalSound().ifPresent(s -> npc.emitEvent(new EntitySoundEvent(npc, s)));
        }
    }

    @Override
    public void onMoveFailed(Npc npc) {
        wrappedAi.onMoveFailed(npc);
    }

    @Override
    public void start(Npc npc) {
        wrappedAi.start(npc);
    }


//    public MonsterWanderingAI(Coordinate destination,
//                              Coordinate previousCoordinate) {
//        this.destination = destination;
//        this.previousCoordinate = previousCoordinate;
//    }
//
//    @Override
//    public void onMoveDone(AbstractMonster monster) {
//        previousCoordinate = monster.coordinate();
//        if (monster.coordinate().equals(destination)) {
//            nextRound(monster);
//        } else {
//            changeToNewState(monster, MonsterCommonState.idle(monster));
//        }
//    }
//
//    @Override
//    public void onMoveFailed(AbstractMonster monster) {
//        monster.emitEvent(SetPositionEvent.of(monster));
//        nextRound(monster);
//    }
//
//    @Override
//    public void onIdleDone(AbstractMonster monster) {
//        changeToNewState(monster, MonsterCommonState.freeze(monster));
//    }
//
//    @Override
//    public void onFrozenDone(AbstractMonster monster) {
//        moveProcess(monster, destination, previousCoordinate, () -> nextRound(monster));
//    }
//
//    @Override
//    public void onHurtDone(AbstractMonster monster) {
//        if (monster.attackSkill() instanceof MonsterRangedAttackSkill skill) {
//            monster.changeAI(new MonsterRangedFightAI(skill));
//        } else {
//            monster.changeAI(new MonsterMeleeFightAI());
//        }
//    }
//
//
//    private void nextRound(AbstractMonster monster) {
//        destination = monster.wanderingArea().random(monster.spawnCoordinate());
//        previousCoordinate = monster.coordinate();
//        changeToNewState(monster, MonsterCommonState.idle(monster));
//    }
//
//    @Override
//    public void start(AbstractMonster monster) {
//        changeToNewState(monster, MonsterCommonState.idle(monster));
//        setSoundTimer();
//    }
//
//    private void setSoundTimer() {
//        timeLeftToSound = ThreadLocalRandom.current().nextInt(15, 25) * 1000L;
//    }
//    @Override
//    public void update(AbstractMonster monster, long delta) {
//        if (monster.normalSound().isEmpty()) {
//            return;
//        }
//        timeLeftToSound = Math.max(0, timeLeftToSound - delta);
//        if (timeLeftToSound == 0) {
//            setSoundTimer();
//            monster.emitEvent(new EntitySoundEvent(monster, monster.normalSound().orElse("")));
//        }
//    }


}
