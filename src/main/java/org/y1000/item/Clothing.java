package org.y1000.item;


public final class Clothing extends AbstractSexualEquipment {

    public Clothing(String name, ItemSdb itemSdb) {
        super(name, itemSdb);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CLOTHING;
    }
}
