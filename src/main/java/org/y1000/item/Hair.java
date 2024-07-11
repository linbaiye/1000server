package org.y1000.item;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class Hair extends AbstractSexualEquipment {

    @Builder
    public Hair(String name, ItemSdb itemSdb) {
        super(name, itemSdb);
    }


    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAIR;
    }
}
