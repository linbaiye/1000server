package org.y1000.item;

public final class Clothing extends AbstractArmorEquipment {
    public Clothing(String name, boolean male) {
        super(name, male);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CLOTHING;
    }
}
