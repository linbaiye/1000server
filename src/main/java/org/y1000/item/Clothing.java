package org.y1000.item;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class Clothing extends AbstractEquipment {
    private final boolean male;

    @Builder
    public Clothing(String name, boolean male, String dropSound, String eventSound, String desc) {
        super(name, dropSound, eventSound, desc);
        this.male = male;
    }

    public boolean isMale() {
        return male;
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CLOTHING;
    }
}
