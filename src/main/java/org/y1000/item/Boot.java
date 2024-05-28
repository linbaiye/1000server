package org.y1000.item;

public final class Boot extends AbstractEquipment {
    public Boot(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.BOOT;
    }
}
