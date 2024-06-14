package org.y1000.entities.creatures.monster;

import org.y1000.sdb.MonsterSdb;

public final class MonsterAttributeProvider implements AttributeProvider {

    private final String name;
    private final MonsterSdb monsterSdb;

    public MonsterAttributeProvider(String name, MonsterSdb monsterSdb) {
        this.name = name;
        this.monsterSdb = monsterSdb;
    }

    @Override
    public int life() {
        return monsterSdb.getLife(name);
    }

    @Override
    public int avoidance() {
        return monsterSdb.getAvoid(name) + 25;
    }

    @Override
    public int recovery() {
        return monsterSdb.getRecovery(name) + 70;
    }

    @Override
    public int attackSpeed() {
        return monsterSdb.getAttackSpeed(name) + 150;
    }

    @Override
    public int wanderingRange() {
        return monsterSdb.getActionWidth(name);
    }

    @Override
    public int armor() {
        return monsterSdb.getArmor(name);
    }

    @Override
    public int hit() {
        return monsterSdb.getAccuracy(name) ;
    }

    @Override
    public String attackSound() {
        return monsterSdb.getSoundAttack(name);
    }

    @Override
    public int damage() {
        return monsterSdb.getDamage(name) ;
    }
}
