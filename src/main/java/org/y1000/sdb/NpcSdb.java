package org.y1000.sdb;

public interface NpcSdb {
    int getRecovery(String name);

    String getAnimate(String name);

    int getAvoid(String name);

    int getAttackSpeed(String name);

    String getSoundAttack(String name);

    boolean attack(String name);

    String getSoundStructed(String name);

    String getSoundDie(String name);

    int getLife(String name);

    int getAccuracy(String name);

    int getDamage(String name);

    int getArmor(String name);

    int getActionWidth(String name);

    int getWalkSpeed(String name);

    String getHaveItem(String name);

    boolean containsName(String name);

    String getViewName(String name);

    String getShape(String name);

    int getRegenInterval(String name);

    default int getEscapeLife(String name) {
        return Integer.MIN_VALUE;
    }

    default String getAttackMagic(String name) {
        return null;
    }
}
