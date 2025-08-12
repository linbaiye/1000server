package org.y1000.entities.objects;

import org.y1000.entities.AbstractDropItemAbility;

import java.util.List;
import java.util.Optional;

public class DynamicObjectDropItemAbility extends AbstractDropItemAbility {
    private final List<Item> items;

    public DynamicObjectDropItemAbility(List<Item> items) {
        this.items = items;
    }

    public void apply(DynamicObject object) {
        object.sendEvent(new DynamicObjectDropItemEvent(items, object));
    }

    public static Optional<DynamicObjectDropItemAbility> parse(String text) {
        List<Item> items1 = parseItems(text);
        return items1.isEmpty() ? Optional.empty() : Optional.of(new DynamicObjectDropItemAbility(items1));
    }
}
