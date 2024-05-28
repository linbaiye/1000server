package org.y1000.item;

public final class Trouser extends AbstractEquipment {

    public Trouser(String name) {
        super(name);
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.TROUSER;
    }

}
