package org.y1000.item;

public final class Boot extends AbstractArmorEquipment {
    public Boot(String name, boolean male) {
        super(name, male);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.BOOT;
    }
}
