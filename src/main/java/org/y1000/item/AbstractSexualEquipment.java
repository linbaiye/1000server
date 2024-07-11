package org.y1000.item;

import lombok.Getter;

@Getter
public abstract class AbstractSexualEquipment extends AbstractEquipment {

    private final boolean male;

    public AbstractSexualEquipment(String name,
                                   ItemSdb itemSdb) {
        super(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name), itemSdb.getDesc(name));
        this.male = itemSdb.isMale(name);
    }

    public AbstractSexualEquipment(String name,
                                   String drop,
                                   String eventSound, String description, boolean male) {
        super(name, drop, eventSound, description);
        this.male = male;
    }
}
