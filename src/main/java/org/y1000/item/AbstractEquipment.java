package org.y1000.item;


import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractEquipment extends AbstractItem implements Equipment {

    private Long id;

    private final Set<Object> abilities;

    private final int originColor;

    public AbstractEquipment(String name,
                             String drop,
                             String eventSound,
                             String description,
                             int color) {
        super(name, ItemType.EQUIPMENT, drop, eventSound, description);
        abilities = new HashSet<>();
        this.originColor = color;
    }

    public AbstractEquipment(String name, ItemSdb itemSdb, Set<Object> abilities) {
        super(name, ItemType.EQUIPMENT, itemSdb);
        this.abilities = abilities != null ? abilities : new HashSet<>();
        this.originColor = itemSdb.getColor(name);
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
            throw new IllegalStateException("Changing id is not allowed.");
        }
        this.id = id;
    }

    @Override
    public int color() {
        return findAbility(Dyable.class)
                .map(Dyable::color)
                .orElse(originColor);
    }

    @Override
    public <T> Optional<T> findAbility(Class<T> type) {
        if (type == null)
            return Optional.empty();
        return abilities.stream().filter(a -> type.isAssignableFrom(a.getClass()))
                .map(type::cast)
                .findFirst();
    }
}
