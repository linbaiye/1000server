package org.y1000.item;

import lombok.Builder;


public final class DefaultStackItem extends AbstractStackItem {

    @Builder
    public DefaultStackItem(String name, long number, ItemType type, String dropSound,
                            String eventSound, String desc) {
        super(name, number, type, dropSound, eventSound, desc);
    }

    public DefaultStackItem(String name, long number) {
        super(name, number, ItemType.STACK, "", "");
    }

    public DefaultStackItem(String name, long number, ItemType type, ItemSdb itemSdb) {
        super(name, number, type, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name), itemSdb.getDesc(name));
    }

    public static DefaultStackItem money(long number) {
        return new DefaultStackItem(MONEY, number, ItemType.MONEY, "", "", "");
    }
    public static final String MONEY = "钱币";

}
