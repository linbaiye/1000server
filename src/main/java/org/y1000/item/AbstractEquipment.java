package org.y1000.item;


import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEquipment extends AbstractItem implements Equipment {

    public AbstractEquipment(String name, String drop, String eventSound,
                             String description) {
        super(name, ItemType.EQUIPMENT, drop, eventSound, description);
    }

    public AbstractEquipment(String name, ItemSdb itemSdb) {
        super(name, ItemType.EQUIPMENT, itemSdb);
    }


    protected StringBuilder getDescriptionBuilder() {
        return StringUtils.isEmpty(super.description()) ? new StringBuilder()
                : new StringBuilder(super.description()).append("\n");
    }

}
