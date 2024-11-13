package org.y1000.item;


import org.y1000.entities.GroundedItem;
import org.y1000.persistence.EquipmentPo;

public interface ItemFactory {

    Item createItem(GroundedItem item);

    Item createItem(String name);

    default StackItem createMoney(long number) {
        return (StackItem) createItem(ItemType.MONEY_NAME, number);
    }

    Item createItem(String name, long number);

    SexualEquipment createTrouser(String name);

    ArmorEquipment createHat(String name);

    ArmorEquipment createChest(String name);

    SexualEquipment createHair(String name);

    ArmorEquipment createBoot(String name);

    ArmorEquipment createWrist(String name);

    SexualEquipment createClothing(String name);

    Equipment createEquipment(String name);

    Equipment createEquipment(String name, int color);

    Equipment createEquipment(EquipmentPo equipmentPo);
}
