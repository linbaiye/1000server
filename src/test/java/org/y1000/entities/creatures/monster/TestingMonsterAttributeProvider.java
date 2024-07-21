package org.y1000.entities.creatures.monster;

import org.y1000.entities.AttributeProvider;

public class TestingMonsterAttributeProvider implements AttributeProvider {
    /*                .attributeProvider()
                .attackSpeed(200)
                .recovery(100)
                .avoidance(0)
                .life(1000)
                .wanderingRange(10)*/

    public int attackSpeed = 100;
    public int recovery = 200;
    public int life;
    public int avoidance;
    public int armor;

    @Override
    public int life() {
        return life;
    }

    @Override
    public int avoidance() {
        return avoidance;
    }

    @Override
    public int recovery() {
        return recovery;
    }

    @Override
    public int attackSpeed() {
        return attackSpeed;
    }

    @Override
    public int wanderingRange() {
        return 10;
    }

    @Override
    public int armor() {
        return armor;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    public int damage() {
        return 0;
    }

    @Override
    public String shape() {
        return "shape";
    }

    @Override
    public String animate() {
        return "animate";
    }

    @Override
    public String hurtSound() {
        return "hurtSound";
    }
}
