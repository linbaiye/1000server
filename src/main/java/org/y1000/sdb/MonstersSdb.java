package org.y1000.sdb;

public interface MonstersSdb {
    int getRecovery(String name);

    String getAnimate(String name);

    int getAvoid(String name);

    int getAttackSpeed(String name);

    String getSoundAttack(String name);

    String getSoundStructed(String name);

    String getViewName(String name);

    String getShape(String name);

    String getSoundStart(String name);

    String getSoundNormal(String name);

    String getSoundDie(String name);

    int getLife(String name);

    int getAccuracy(String name);

    int getDamage(String name);

    int getArmor(String name);

    int getActionWidth(String name);

    String getAttackMagic(String name);

    int getWalkSpeed(String name);

    boolean isPassive(String name);

    String getHaveItem(String name);

    boolean contains(String name);

    boolean attack(String name);

    String getHaveMagic(String name);

    int getViewWidth(String name);

    int getEscapeLife(String name);

    int getRegenInterval(String name);
}
