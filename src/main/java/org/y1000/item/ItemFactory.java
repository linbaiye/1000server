package org.y1000.item;


import org.y1000.entities.GroundedItem;

public interface ItemFactory {

    Item createItem(GroundedItem item);

    Item createItem(String name);

    Item createItem(String name, int number);

}
