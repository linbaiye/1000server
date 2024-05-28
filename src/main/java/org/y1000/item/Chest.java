package org.y1000.item;

public final class Chest extends AbstractEquipment {
    public Chest(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CHEST;
    }
}
