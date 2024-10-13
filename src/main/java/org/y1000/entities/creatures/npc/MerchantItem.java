package org.y1000.entities.creatures.npc;

import lombok.Getter;

public final class MerchantItem {
    private final String name;
    private final int price;
    @Getter
    private final int icon;
    @Getter
    private final int color;

    private final boolean stack;

    public MerchantItem(String name, int price) {
        this(name, price, 0, 0, false);
    }

    public MerchantItem(String name, int price, int icon, int color, boolean canStack) {
        this.name = name;
        this.price = price;
        this.icon = icon;
        this.color = color;
        this.stack = canStack;
    }

    public boolean canStack() {
        return stack;
    }

    public String name() {
        return name;
    }

    public int price() {
        return price;
    }
}
