package org.y1000.item;


public abstract class AbstractEquipment extends AbstractItem implements Equipment {

    public AbstractEquipment(String name) {
        super(name, ItemType.EQUIPMENT);
    }

}
