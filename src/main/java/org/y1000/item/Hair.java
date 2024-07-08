package org.y1000.item;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class Hair extends AbstractEquipment {

    private final boolean male;

    @Builder
    public Hair(String name, boolean male, String dropSound, String eventSound) {
        super(name, dropSound, eventSound);
        this.male = male;
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAIR;
    }
}
