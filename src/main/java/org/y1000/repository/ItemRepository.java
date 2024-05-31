package org.y1000.repository;

import org.y1000.item.Item;

public interface ItemRepository {

    void save(long playerId, int slot, Item item);
}
