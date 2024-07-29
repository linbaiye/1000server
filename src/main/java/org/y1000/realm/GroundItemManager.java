package org.y1000.realm;

import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEventListener;
import org.y1000.util.Coordinate;

public interface GroundItemManager extends ActiveEntityManager<GroundedItem>,
        EntityEventListener {

    void pickItem(Player picker, long id);

    void dropItem(String itemNumberRateArray, Coordinate at);

    void dropItem(String name, int number, Coordinate at);

}
