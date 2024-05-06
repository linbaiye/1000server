package org.y1000.entities.creatures;

public final class PassiveMonsterHurtState extends AbstractCreatureHurtState<PassiveMonster> {

    private final Creature attacker;

    private final int spriteNumber;

    private final int millisPerSprite;

    private final int total;

    public PassiveMonsterHurtState(Creature attacker, int spriteNumber,
                                   int millisPerSprite) {
        this.attacker = attacker;
        this.spriteNumber = spriteNumber;
        this.millisPerSprite = millisPerSprite;
        total = spriteNumber * millisPerSprite;
    }

    public Creature attacker() {
        return attacker;
    }

    @Override
    public void update(PassiveMonster monster, int delta) {
        elapse(delta);
        if (elapsedMillis() >= total) {
            monster.AI().nextMove();
        }
    }
}
