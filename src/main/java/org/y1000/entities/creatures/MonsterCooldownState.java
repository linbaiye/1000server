package org.y1000.entities.creatures;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MonsterCooldownState extends AbstractMonsterState {

    private final Creature attacker;

    public MonsterCooldownState(int totalMillis, Creature attacker) {
        super(totalMillis, State.IDLE);
        this.attacker = attacker;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.retaliate(attacker);
    }

    @Override
    public void afterHurt(PassiveMonster monster, Creature attacker) {
        monster.changeState(new MonsterCooldownState(monster.cooldown(), this.attacker));
    }
}
