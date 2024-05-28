package org.y1000.item;

public final class Hair extends AbstractEquipment {
    public Hair(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAIR;
    }
}
