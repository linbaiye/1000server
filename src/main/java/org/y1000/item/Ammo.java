package org.y1000.item;


import lombok.Getter;

public final class Ammo extends AbstractItem {

    @Getter
    private final String flySprite;

    public Ammo(String name, ItemType type, ItemSdb itemSdb) {
        super(name, type, itemSdb);
        flySprite = "y" + itemSdb.getActionImage(name);
    }

}
