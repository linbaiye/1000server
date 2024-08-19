package org.y1000.item;



public final class ArmorEquipmentImpl extends AbstractArmorEquipment {

    public ArmorEquipmentImpl(String name,
                              ArmorItemAttributeProvider attributeProvider,
                              EquipmentType type) {
        super(name, attributeProvider.dropSound(),
                attributeProvider.eventSound(), attributeProvider.description(), attributeProvider.isMale(), attributeProvider, type);
    }

    @Override
    public int color() {
        return attributeProvider().color();
    }
}
