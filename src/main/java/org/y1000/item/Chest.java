package org.y1000.item;

public final class Chest extends AbstractArmorEquipment {
    public Chest(String name, ArmorItemAttributeProvider attributeProvider) {
        super(name, attributeProvider);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.CHEST;
    }
}
