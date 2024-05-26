package org.y1000.item;

import org.apache.commons.lang3.Validate;


public abstract class AbstractItem implements Item {
    private final String name;
    private final ItemType type;

    public AbstractItem(String name, ItemType type) {
        Validate.notNull(name, "name must not be null.");
        Validate.notNull(type, "type must not be null.");
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ItemType type() {
        return type;
    }
}
