package org.y1000.item;


public abstract class AbstractEquipment extends AbstractItem implements Equipment {

    public AbstractEquipment(String name, String drop, String eventSound) {
        super(name, ItemType.EQUIPMENT, drop, eventSound);
    }

}
