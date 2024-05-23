package org.y1000.entities.item;

import org.apache.commons.lang3.Validate;


public abstract  class AbstractItem implements Item{
    private final long id;
    private final String name;
    private final ItemType type;

    public AbstractItem(long id, String name, ItemType type) {
        Validate.notNull(name, "name must not be null.");
        Validate.notNull(name, "type must not be null.");
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public long id() {
        return id;
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
