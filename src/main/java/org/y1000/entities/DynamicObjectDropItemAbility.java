package org.y1000.entities;

import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.objects.DynamicObjectDropItemEvent;

import java.util.List;
import java.util.Optional;

public class DynamicObjectDropItemAbility extends AbstractDopItemAbility {
    private final List<Item> items;

    public DynamicObjectDropItemAbility(List<Item> items) {
        this.items = items;
    }

    public void apply(DynamicObject object) {
        object.sentEvent(new DynamicObjectDropItemEvent(items, object));
    }

    public static Optional<DynamicObjectDropItemAbility> parse(String text) {
        List<Item> items1 = parseItems(text);
        return items1.isEmpty() ? Optional.empty() : Optional.of(new DynamicObjectDropItemAbility(items1));
    }
}
