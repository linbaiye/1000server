package org.y1000.realm;

import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEventListener;

public interface GroundItemManager extends EntityManager<GroundedItem>,
        EntityEventListener {

    void pickItem(Player picker, long id);
}
