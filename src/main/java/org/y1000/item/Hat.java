package org.y1000.item;

public final class Hat extends AbstractArmorEquipment {
    public Hat(String name, boolean male) {
        super(name, male);
    }

    public Hat(String name) {
        super(name, true);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAT;
    }
}
