package org.y1000.realm;

import org.y1000.entities.objects.IDynamicObject;
import org.y1000.entities.players.Player;

public interface DynamicObjectManager extends ActiveEntityManager<IDynamicObject> {

    DynamicObjectManager EMPTY = EmptyDynamicObjectmanager.INSTANCE;
    
    void init();

    void triggerDynamicObject(long id, Player player, int useSlot);

}
