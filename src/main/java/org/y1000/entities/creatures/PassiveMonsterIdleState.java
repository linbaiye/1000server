package org.y1000.entities.creatures;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PassiveMonsterIdleState extends AbstractCreatureIdleState<PassiveMonster> {

    public PassiveMonsterIdleState(int length) {
        super(length);
    }

    @Override
    public void update(PassiveMonster passiveMonster,
                       int delta) {
        if (resetIfElapsedLength(delta)) {
            passiveMonster.AI().nextMove();
        }
    }

    public static PassiveMonsterIdleState buffalo() {
        return new PassiveMonsterIdleState(2000);
    }

    @Override
    public void getAttacked(PassiveMonster monster, Creature attacker) {
        monster.getAttacked(attacker);
    }
}
