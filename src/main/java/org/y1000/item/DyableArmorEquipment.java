package org.y1000.item;


public final class DyableArmorEquipment extends AbstractArmorEquipment implements DyableEquipment {

    private int color;


    public DyableArmorEquipment(String name,
                              ArmorItemAttributeProvider attributeProvider,
                              EquipmentType type,
                                int color) {
        super(name, attributeProvider.dropSound(),
                attributeProvider.eventSound(), attributeProvider.description(), attributeProvider.isMale(), attributeProvider, type);
        this.color = color;
    }

    @Override
    public void dye(int color) {
        this.color = color;
    }

    @Override
    public void bleach(int color) {
        this.color += color;
    }

    @Override
    public int color() {
        return color;
    }
}
