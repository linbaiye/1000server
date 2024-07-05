package org.y1000.item;


import org.y1000.entities.GroundedItem;

public interface ItemFactory {

    Item createItem(GroundedItem item);

    Item createItem(String name);

    Item createItem(String name, long number);

    Trouser createTrouser(String name);

    Hat createHat(String name);

    Chest createChest(String name);

    Hair createHair(String name);

    Boot createBoot(String name);

    Wrist createWrist(String name);

    Clothing createClothing(String name);

    Equipment createEquipment(String name);
}
