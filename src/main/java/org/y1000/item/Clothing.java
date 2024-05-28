package org.y1000.item;

public final class Clothing extends AbstractEquipment {
    public Clothing(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CLOTHING;
    }
}
