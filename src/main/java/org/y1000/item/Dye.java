package org.y1000.item;


public final class Dye extends AbstractItem {
    private final int color;
    public Dye(String name, ItemSdb itemSdb) {
        super(name, ItemType.DYE, itemSdb);
        color = itemSdb.getColor(name);
    }

    @Override
    public int color() {
        return color;
    }
}
