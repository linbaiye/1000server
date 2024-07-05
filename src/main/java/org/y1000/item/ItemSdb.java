package org.y1000.item;

import org.y1000.kungfu.attack.AttackKungFuType;

public interface ItemSdb {
    EquipmentType getEquipmentType(String itemName);

    AttackKungFuType getAttackKungFuType(String item);

    ItemType getType(String itemName);

    String getSoundEvent(String itemName);

    String getSoundDrop(String itemName);

    int getPrice(String itemName);

    int getBuyPrice(String itemName);

    boolean isMale(String itemName);

}
