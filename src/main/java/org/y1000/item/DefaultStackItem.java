package org.y1000.item;

import lombok.Builder;


public final class DefaultStackItem extends AbstractStackItem {
    @Builder
    public DefaultStackItem(String name, long number, ItemType type, String dropSound,
                            String eventSound) {
        super(name, number, type, dropSound, eventSound);
    }

    public DefaultStackItem(String name, long number) {
        super(name, number, ItemType.STACK, "", "");
    }

    public static DefaultStackItem money(long number, String dropSound, String eventSound) {
        return new DefaultStackItem(MONEY, number, ItemType.MONEY, dropSound, eventSound);
    }

    public static DefaultStackItem money(long number) {
        return new DefaultStackItem(MONEY, number, ItemType.MONEY, "", "");
    }

    public static final String MONEY = "钱币";


}
