package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.apache.commons.lang3.Validate;

@Getter
public class NpcTradeItem {

    private final MerchantItem item;

    private int cost;

    private int number;

    private final int inventorySlot;

    public NpcTradeItem(MerchantItem item, int inventorySlot) {
        this.item = item;
        this.inventorySlot = inventorySlot;
        number = 0;
        cost = 0;
    }

    public void addNumber(int n) {
        Validate.isTrue(n > 0);
        number += n;
        cost = number * item.price();
    }

    public boolean nameEquals(String name) {
        return item.name().equals(name);
    }
}
