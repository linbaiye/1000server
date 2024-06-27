package org.y1000.sdb;

public final class NpcSdbImpl extends AbstractSdbReader implements NpcSdb {
    public static final NpcSdbImpl Instance = new NpcSdbImpl();
    private NpcSdbImpl() {

    }

    @Override
    public int getRecovery(String name) {
        return getInt(name, "Recovery");
    }


    @Override
    public String getAnimate(String name) {
        return get(name, "animate");
    }

    @Override
    public int getAvoid(String name) {
        return getIntOrZero(name, "Avoid");
    }

    @Override
    public int getAttackSpeed(String name) {
        return getInt(name, "AttackSpeed");
    }

    @Override
    public String getSoundAttack(String name) {
        return get(name, "SoundAttack");
    }

    @Override
    public String getSoundStructed(String name) {
        return get(name, "SoundStructed");
    }

    @Override
    public String getSoundStart(String name) {
        return get(name, "SoundStart");
    }

    @Override
    public String getSoundNormal(String name) {
        return get(name, "SoundNormal");
    }

    @Override
    public String getSoundDie(String name) {
        return get(name, "SoundDie");
    }


    @Override
    public int getLife(String name) {
        return getInt(name, "Life");
    }

    @Override
    public int getAccuracy(String name) {
        return getIntOrZero(name, "Accuracy");
    }

    @Override
    public int getDamage(String name) {
        return getIntOrZero(name, "Damage");
    }

    @Override
    public int getArmor(String name) {
        return getIntOrZero(name, "Armor");
    }

    @Override
    public int getActionWidth(String name) {
        return getInt(name, "ActionWidth");
    }

    @Override
    public String getAttackMagic(String name) {
        return get(name, "AttackMagic");
    }


    @Override
    public int getWalkSpeed(String name) {
        return getInt(name, "WalkSpeed");
    }


    @Override
    public String getHaveItem(String name) {
        return getOrNull(name, "HaveItem");

    }
}