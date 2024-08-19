package org.y1000.item;

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
        this.color += color;
    }

    @Override
    public void bleach(int color) {
        this.color = color;
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
