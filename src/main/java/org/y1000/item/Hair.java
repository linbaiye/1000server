package org.y1000.item;

public final class Hair extends AbstractArmorEquipment {
    public Hair(String name, boolean male) {
        super(name, male);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAIR;
    }
}
