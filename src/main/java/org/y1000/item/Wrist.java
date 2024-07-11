package org.y1000.item;

public final class Wrist extends AbstractArmorEquipment {

    public Wrist(String name, ArmorItemAttributeProvider attributeProvider) {
        super(name, attributeProvider);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WRIST;
    }
}
