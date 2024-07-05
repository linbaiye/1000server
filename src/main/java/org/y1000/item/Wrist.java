package org.y1000.item;

public final class Wrist extends AbstractArmorEquipment {
    public Wrist(String name, boolean male) {
        super(name, male);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WRIST;
    }
}
