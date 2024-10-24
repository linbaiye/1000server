package org.y1000.item;

import org.apache.commons.lang3.Validate;

import java.util.Set;

public final class SexualEquipmentImpl extends AbstractSexualEquipment {

    private final EquipmentType type;

    public SexualEquipmentImpl(String name,
                               ItemSdb itemSdb,
                               EquipmentType type,
                               Set<Object> abilities) {
        super(name, itemSdb, abilities);
        Validate.notNull(type);
        this.type = type;
    }

    @Override
    public EquipmentType equipmentType() {
        return type;
    }
}
