package org.y1000.entities.objects;

import org.y1000.entities.AbstractDopItemAbility;
import org.y1000.realm.DynamicObjectEventHandler;

import java.util.List;

public class DynamicObjectDropItemEvent implements DynamicObjectEvent {

    private final List<AbstractDopItemAbility.Item> itemList;

    private final DynamicObject object;

    public DynamicObjectDropItemEvent(List<AbstractDopItemAbility.Item> itemList, DynamicObject object) {
        this.itemList = itemList;
        this.object = object;
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        itemList.forEach(i -> handler.dropItem(i.name(), i.number(), object.coordinate()));
    }

    @Override
    public DynamicObject source() {
        return object;
    }
}
