package org.y1000.item;



public final class Dye extends AbstractItem {
    private final int color;
    public Dye(String name, ItemSdb itemSdb) {
        super(name, ItemType.DYE, itemSdb);
        color = itemSdb.getColor(name);
    }

    public void dye(Dyable dyable) {
        if (dyable == null) {
            return;
        }
        if ("脱色药".equals(name())) {
            dyable.bleach(color);
        } else {
            dyable.dye(color);
        }
    }

    @Override
    public int color() {
        return color;
    }
}
