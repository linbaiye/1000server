package org.y1000.entities.creatures.monster;

public interface MonsterAI {

    void onMoveDone(AbstractMonster monster);

    void onMoveFailed(AbstractMonster monster);

    void onIdleDone(AbstractMonster monster);

    default void onAttackDone(AbstractMonster monster) {

    }

    void onFrozenDone(AbstractMonster monster);

    void onHurtDone(AbstractMonster monster);

    void start(AbstractMonster monster);

    default void update(AbstractMonster monster, long delta) {

    }

}
