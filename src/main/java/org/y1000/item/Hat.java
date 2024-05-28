package org.y1000.item;

public final class Hat extends AbstractEquipment {
    public Hat(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAT;
    }
}
