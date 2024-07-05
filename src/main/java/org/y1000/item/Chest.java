package org.y1000.item;

public final class Chest extends AbstractArmorEquipment {
    public Chest(String name, boolean male) {
        super(name, male);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CHEST;
    }
}
