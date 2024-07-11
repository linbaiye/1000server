package org.y1000.item;

import lombok.Getter;

@Getter
public final class Trouser extends AbstractSexualEquipment {

    public Trouser(String name, ItemSdb itemSdb) {
        super(name, itemSdb);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.TROUSER;
    }

}
