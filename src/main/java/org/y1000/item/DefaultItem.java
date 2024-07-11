package org.y1000.item;

import lombok.Builder;

public final class DefaultItem extends AbstractItem {
    @Builder
    public DefaultItem(String name,
                       String dropSound, String eventSound, String desc) {
        super(name, ItemType.SINGLE, dropSound, eventSound, desc);
    }
}
