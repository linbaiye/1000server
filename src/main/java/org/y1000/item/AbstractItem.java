package org.y1000.item;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Optional;


public abstract class AbstractItem implements Item {
    private final String name;
    private final ItemType type;
    private final String dropSound;
    private final String eventSound;
    private final String description;
    public AbstractItem(String name,
                        ItemType type,
                        String dropSound,
                        String eventSound,
                        String description) {
        Validate.notNull(name, "viewName must not be null.");
        Validate.notNull(type, "type must not be null.");
        this.name = name;
        this.type = type;
        this.dropSound = StringUtils.isEmpty(dropSound) ? null : dropSound;
        this.eventSound  = StringUtils.isEmpty(eventSound) ? null : eventSound;
        this.description = description != null ? description : "";
    }

    public AbstractItem(String name, ItemType type, ItemSdb itemSdb) {
        this.name = name;
        this.type = type;
        this.dropSound = StringUtils.isEmpty(itemSdb.getSoundDrop(name)) ? null : itemSdb.getSoundDrop(name);
        this.eventSound = StringUtils.isEmpty(itemSdb.getSoundEvent(name)) ? null : itemSdb.getSoundEvent(name);
        this.description = itemSdb.getDesc(name) != null ? itemSdb.getDesc(name) : "";
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public Optional<String> dropSound() {
        return Optional.ofNullable(dropSound);
    }

    @Override
    public Optional<String> eventSound() {
        return Optional.ofNullable(eventSound);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public ItemType itemType() {
        return type;
    }

}
