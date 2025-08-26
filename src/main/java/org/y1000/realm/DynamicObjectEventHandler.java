package org.y1000.realm;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.objects.DynamicObjectRemoveEvent;
import org.y1000.util.Coordinate;

public interface DynamicObjectEventHandler extends EntityEventHandler {

    void onRemove(DynamicObjectRemoveEvent event);

    void onRespawn(DynamicObject object);

    void dropItem(String name, int number, Coordinate dropAt);

    void callNpc(String npcName, ActiveEntity enemy, Coordinate callAt);
}
