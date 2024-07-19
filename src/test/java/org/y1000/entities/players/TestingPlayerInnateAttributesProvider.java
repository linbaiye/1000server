package org.y1000.entities.players;

import lombok.Builder;

public class TestingPlayerInnateAttributesProvider implements PlayerInnateAttributesProvider {
    private final int avoid;
    private final Damage damage;
    private final int speed;

    private final int recovery;
    @Builder
    public TestingPlayerInnateAttributesProvider(int avoid,
                                                 Damage damage, int speed,
                                                 int recovery) {
        this.avoid = avoid;
        this.damage = damage == null ? Damage.DEFAULT : damage;
        this.speed = speed;
        this.recovery = recovery;
    }

    @Override
    public int avoidance() {
        return avoid;
    }

    @Override
    public int recovery() {
        return recovery;
    }

    @Override
    public int attackSpeed() {
        return speed;
    }

    @Override
    public Damage damage() {
        return damage;
    }
}
