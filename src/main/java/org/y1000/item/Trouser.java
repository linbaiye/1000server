package org.y1000.item;

public final class Trouser extends AbstractArmorEquipment {

    public Trouser(String name, boolean male) {
        super(name, male);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.TROUSER;
    }

}
