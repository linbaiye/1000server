package org.y1000.item;


import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractEquipment extends AbstractItem implements Equipment {

    private Long id;

    private final Set<Object> abilities;

    public AbstractEquipment(String name, String drop, String eventSound,
                             String description) {
        super(name, ItemType.EQUIPMENT, drop, eventSound, description);
        abilities = new HashSet<>();
    }

    public AbstractEquipment(String name, ItemSdb itemSdb) {
        super(name, ItemType.EQUIPMENT, itemSdb);
        abilities = new HashSet<>();
    }

    protected StringBuilder getDescriptionBuilder() {
        return StringUtils.isEmpty(super.description()) ? new StringBuilder()
                : new StringBuilder(super.description()).append("\n");
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(long id) {
        if (this.id != null) {
            throw new IllegalStateException();
        }
        this.id = id;
    }
}
