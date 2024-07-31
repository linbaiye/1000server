package org.y1000.realm;

import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.players.Player;

public interface DynamicObjectManager extends ActiveEntityManager<DynamicObject> {
    EmptyDynamicObjectManager EMPTY = EmptyDynamicObjectManager.INSTANCE;

    void init(RealmMap map);

    void triggerDynamicObject(long id, Player player, int useSlot);

}
