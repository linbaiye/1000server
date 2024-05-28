package org.y1000.item;

public final class Wrist extends AbstractEquipment{
    public Wrist(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WRIST;
    }
}
