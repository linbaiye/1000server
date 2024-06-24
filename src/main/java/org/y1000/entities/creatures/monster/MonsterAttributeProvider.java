package org.y1000.entities.creatures.monster;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.sdb.*;

import java.util.Optional;

public final class MonsterAttributeProvider implements AttributeProvider {

    private final String name;
    private final MonstersSdb monsterSdb;

    public MonsterAttributeProvider(String name, MonstersSdb monsterSdb) {
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
    public Optional<String> attackSound() {
        return getSound(monsterSdb.getSoundAttack(name));
    }

    @Override
    public int damage() {
        return monsterSdb.getDamage(name) ;
    }

    @Override
    public String hurtSound() {
        return monsterSdb.getSoundStructed(name);
    }

    @Override
    public int walkSpeed() {
        return monsterSdb.getWalkSpeed(name) * 10;
    }

    private Optional<String> getSound(String s) {
        return StringUtils.isEmpty(s) ? Optional.empty() : Optional.of(s);
    }

    @Override
    public Optional<String> normalSound() {
        var s = monsterSdb.getSoundNormal(name);
        return getSound(s);
    }



    @Override
    public Optional<String> dieSound() {
        return getSound(monsterSdb.getSoundDie(name));
    }
}
