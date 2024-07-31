package org.y1000.item;


public final class Ammo extends AbstractItem {
    private final int spriteId;
    public Ammo(String name, ItemType type, ItemSdb itemSdb) {
        super(name, type, itemSdb);
        spriteId = itemSdb.getActionImage(name);
    }

    public int spriteId() {
        return spriteId;
    }
}
