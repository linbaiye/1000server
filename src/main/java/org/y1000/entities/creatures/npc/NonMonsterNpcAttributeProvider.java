package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.AttributeProvider;
import org.y1000.sdb.NpcSdb;

import java.util.Optional;

public final class NonMonsterNpcAttributeProvider implements AttributeProvider {

    private final String name;
    private final NpcSdb npcSdb;

    public NonMonsterNpcAttributeProvider(String name, NpcSdb npcSdb) {
        this.name = name;
        this.npcSdb = npcSdb;
    }

    @Override
    public int life() {
        return npcSdb.getLife(name);
    }

    @Override
    public int avoidance() {
        return npcSdb.getAvoid(name) + 25;
    }

    @Override
    public int recovery() {
        return npcSdb.getRecovery(name) + 70;
    }

    @Override
    public int attackSpeed() {
        return npcSdb.getAttackSpeed(name) + 150;
    }

    @Override
    public int wanderingRange() {
        return npcSdb.getActionWidth(name);
    }

    @Override
    public int armor() {
        return npcSdb.getArmor(name);
    }

    @Override
    public int hit() {
        return npcSdb.getAccuracy(name) ;
    }

    @Override
    public int viewWidth() {
        return 0;
    }

    @Override
    public Optional<String> attackSound() {
        return getSound(npcSdb.getSoundAttack(name));
    }

    @Override
    public int damage() {
        return npcSdb.getDamage(name) ;
    }

    @Override
    public String hurtSound() {
        return npcSdb.getSoundStructed(name);
    }

    @Override
    public int walkSpeed() {
        return 2000;
    }

    @Override
    public String idName() {
        return name;
    }

    private Optional<String> getSound(String s) {
        return StringUtils.isEmpty(s) ? Optional.empty() : Optional.of(s);
    }

    @Override
    public Optional<String> normalSound() {
        var s = npcSdb.getSoundNormal(name);
        return getSound(s);
    }

    @Override
    public String animate() {
        return npcSdb.getAnimate(name);
    }

    @Override
    public String shape() {
        return npcSdb.getShape(name);
    }

    public int image() {
        return npcSdb.getImage(name);
    }


    @Override
    public Optional<String> dieSound() {
        return getSound(npcSdb.getSoundDie(name));
    }
}
