package org.y1000.item;

public final class Hat extends AbstractArmorEquipment {
    public Hat(String name, ArmorItemAttributeProvider attributeProvider) {
        super(name, attributeProvider);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.HAT;
    }
}
