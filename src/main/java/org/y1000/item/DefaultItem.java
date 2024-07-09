package org.y1000.item;

public final class DefaultItem extends AbstractItem {
    public DefaultItem(String name,
                       String dropSound, String eventSound) {
        super(name, ItemType.SINGLE, dropSound, eventSound);
    }
}
