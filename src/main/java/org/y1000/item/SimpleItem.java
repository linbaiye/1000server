package org.y1000.item;

public final class SimpleItem extends AbstractItem {
    public SimpleItem(String name, ItemType type, ItemSdb itemSdb) {
        super(name, type, itemSdb);
    }

    public static SimpleItem uncategoried(String name, ItemSdb itemSdb) {
        return new SimpleItem(name, ItemType.UNCATEGORIED, itemSdb);
    }

    public String toString() {
        return name();
    }
}
