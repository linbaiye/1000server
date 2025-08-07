package org.y1000.sdb;

public abstract class AbstractNpcSdb extends AbstractCSVSdbReader implements NpcSdb {
    @Override
    public int getRecovery(String name) {
        return getInt(name, "Recovery");
    }

    @Override
    public String getAnimate(String name) {
        return get(name, "Animate");
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
    public boolean attack(String name) {
        return "TRUE".equals(get(name, "boAttack"));
    }

    @Override
    public String getSoundStructed(String name) {
        return get(name, "SoundStructed");
    }

    @Override
    public String getViewName(String name) {
        return get(name, "ViewName");
    }

    @Override
    public String getShape(String name) {
        return get(name, "Shape");
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
    public int getWalkSpeed(String name) {
        return getIntOrZero(name, "WalkSpeed");
    }

    @Override
    public String getHaveItem(String name) {
        return getOrNull(name, "HaveItem");
    }

    public int getRegenInterval(String name) {
        return getIntOrZero(name, "RegenInterval");
    }

    @Override
    public boolean containsName(String name) {
        return uniqueIds().contains(name);
    }
}
