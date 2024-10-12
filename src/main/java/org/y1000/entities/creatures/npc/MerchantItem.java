package org.y1000.entities.creatures.npc;

import lombok.Getter;

public final class MerchantItem {
    private final String name;
    private final int price;
    @Getter
    private final int icon;
    @Getter
    private final int color;

    public MerchantItem(String name, int price) {
        this(name, price, 0, 0);
    }

    public MerchantItem(String name, int price, int icon, int color) {
        this.name = name;
        this.price = price;
        this.icon = icon;
        this.color = color;
    }

    public String name() {
        return name;
    }

    public int price() {
        return price;
    }
}
