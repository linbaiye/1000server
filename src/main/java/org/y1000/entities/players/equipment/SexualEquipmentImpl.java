package org.y1000.entities.players.equipment;

import org.apache.commons.lang3.Validate;
import org.y1000.item.ItemSdb;

import java.util.Set;

public final class SexualEquipmentImpl extends AbstractSexualEquipment {

    private final EquipmentType type;

    public SexualEquipmentImpl(String name,
                               ItemSdb itemSdb,
                               EquipmentType type,
                               Set<EquipmentAbility> abilities) {
        super(name, itemSdb, abilities);
        Validate.notNull(type);
        this.type = type;
    }

    @Override
    public EquipmentType equipmentType() {
        return type;
    }
}
