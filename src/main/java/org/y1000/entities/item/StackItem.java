package org.y1000.entities.item;

public interface StackItem extends Item {
    int number();

    void stack(Item item);

    boolean canStack(Item item);
}
