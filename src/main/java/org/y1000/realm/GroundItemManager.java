package org.y1000.realm;

import org.y1000.entities.GroundItem;
import org.y1000.item.Item;
import org.y1000.util.Coordinate;

public interface GroundItemManager extends ActiveEntityManager<GroundItem> {

    void dropItem(String name, int number, Coordinate at);

    void dropItem(Item item, Coordinate droppedAt);

}
