package org.y1000.item;

public final class Boot extends AbstractArmorEquipment {

    public Boot(String name, ArmorItemAttributeProvider attributeProvider) {
        super(name, attributeProvider);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.BOOT;
    }
}
