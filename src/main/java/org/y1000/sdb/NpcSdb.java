package org.y1000.sdb;

public interface NpcSdb {
    int getRecovery(String name);

    String getAnimate(String name);

    int getAvoid(String name);

    int getAttackSpeed(String name);

    String getSoundAttack(String name);

    String getSoundStructed(String name);

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

    String getHaveItem(String name);

    boolean contains(String name);

    boolean isProtector(String name);

    String getNpcText(String name);

    boolean isSeller(String name);

    boolean isBanker(String name);

    String getViewName(String name);

    String getShape(String name);

    int getViewWidth(String name);

    boolean isQuester(String name);
}

