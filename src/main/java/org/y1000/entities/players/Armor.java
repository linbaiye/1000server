package org.y1000.entities.players;

public record Armor(int body, int head, int arm, int leg) {
    public Armor add(Armor another) {
        return another != null && another != Empty ?
                new Armor(body + another.body, head + another.head, arm + another.arm, leg + another.leg)
                : this;
    }
    public static final Armor Empty = new Armor(0, 0, 0,  0);
}
