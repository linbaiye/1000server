package org.y1000.item;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DecorativeEquipment extends AbstractSexualEquipment implements DyableEquipment{
    private final EquipmentType equipmentType;
    private int color;

    public DecorativeEquipment(String name, EquipmentType equipmentType, ItemSdb itemSdb, int color) {
        super(name, itemSdb);
        this.equipmentType = equipmentType;
        this.color = color;
    }

    @Override
    public void dye(int color) {
        this.color = color;
    }

    @Override
    public void bleach(int color) {
        this.color += color;
        this.color %= 256;
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public EquipmentType equipmentType() {
        return equipmentType;
    }
}
