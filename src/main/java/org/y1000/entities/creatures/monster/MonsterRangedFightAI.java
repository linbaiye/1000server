package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.event.CreatureAttackEvent;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class MonsterRangedFightAI extends AbstractMonsterFightAI {
//
//    private final MonsterRangedAttackSkill skill;
//
//
//    public MonsterRangedFightAI(MonsterRangedAttackSkill skill) {
//        this.skill = skill;
//    }
//
//    @Override
//    public void onMoveDone(AbstractMonster monster) {
//        process(monster);
//    }
//
//    @Override
//    public void onMoveFailed(AbstractMonster monster) {
//        process(monster);
//    }
//
//    @Override
//    public void onIdleDone(AbstractMonster monster) {
//        process(monster);
//    }
//
//    @Override
//    public void onFrozenDone(AbstractMonster monster) {
//        process(monster);
//    }
//
//    @Override
//    public void onAttackDone(AbstractMonster monster) {
//        if (monster.getFightingEntity() == null) {
//            monster.changeAI(MonsterWanderingAI.create(monster));
//            return;
//        }
//        int distance = monster.getFightingEntity().coordinate().directDistance(monster.coordinate());
//        if (distance <= 1 && ThreadLocalRandom.current().nextInt() % 2 == 1) {
//            doMeleeAttack(monster);
//        } else if (monster.getFightingEntity().coordinate().directDistance(monster.coordinate()) <= 2) {
//            monster.changeAI(new MonsterEscapeAI(this));
//        } else {
//            process(monster);
//        }
//    }
//
//
//    private void process(AbstractMonster monster) {
//        if (monster.getFightingEntity() == null) {
//            monster.changeAI(MonsterWanderingAI.create(monster));
//        } else {
//            attackProcess(monster, () -> doRangedAttack(monster));
//        }
//    }
//
//    private void doRangedAttack(AbstractMonster monster) {
//        monster.cooldownAttack();
//        monster.changeState(MonsterRangedAttackState.attack(monster, skill.sound(), skill.projectileSpriteId()));
//        monster.emitEvent(CreatureAttackEvent.ofMonster(monster));
//    }

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
//        process(monster);
    }
}
