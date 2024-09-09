package org.y1000.item;

import org.y1000.kungfu.attack.AttackKungFuType;

public interface ItemSdb {
    EquipmentType getEquipmentType(String itemName);

    AttackKungFuType getAttackKungFuType(String item);

    boolean canStack(String itemName);

    ItemType getType(String itemName);

    String getSoundEvent(String itemName);

    String getSoundDrop(String itemName);

    int getPrice(String itemName);

    int getBuyPrice(String itemName);

    boolean isMale(String itemName);

    int getAvoid(String itemName);

    int getActionImage(String itemName);

    int getAttackSpeed(String itemName);

    int getRecovery(String itemName);

    int getDamageBody(String name);

    int getDamageHead(String name);

    int getDamageArm(String name);

    int getDamageLeg(String name);

    int getArmorBody(String name);

    int getArmorHead(String name);

    int getArmorArm(String name);

    int getArmorLeg(String name);

    String getDesc(String name);

    Integer getColor(String name);

    default int getColorOrZero(String name) {
        return getColor(name) != null ? getColor(name) : 0;
    }

    boolean isColoring(String name);
}
