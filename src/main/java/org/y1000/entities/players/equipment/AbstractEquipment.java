package org.y1000.entities.players.equipment;


import org.apache.commons.lang3.StringUtils;
import org.y1000.item.AbstractItem;
import org.y1000.item.ItemSdb;
import org.y1000.item.ItemType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractEquipment extends AbstractItem implements Equipment {

    private Long id;

    private final Set<EquipmentAbility> abilities;

    private final int originColor;

    private final String wearShape;


    public AbstractEquipment(String name, ItemSdb itemSdb, Set<EquipmentAbility> abilities) {
        super(name, ItemType.EQUIPMENT, itemSdb);
        this.abilities = abilities != null ? abilities : Collections.emptySet();
        this.originColor = itemSdb.getColor(name);
        this.wearShape = itemSdb.getWearShape(name);
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

    @Override
    public String wearShape() {
        return wearShape;
    }
}
