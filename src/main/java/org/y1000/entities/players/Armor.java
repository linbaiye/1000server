package org.y1000.entities.players;


public record Armor(int body, int head, int arm, int leg) {
    public Armor add(Armor another) {
        return another != null && another != Zero ?
                new Armor(body + another.body, head + another.head, arm + another.arm, leg + another.leg)
                : this;
    }
    public static final Armor Zero = new Armor(0, 0, 0,  0);

    public Armor multiply(float f) {
        return new Armor((int) (body * f), (int) (head * f), (int) (arm * f), (int) (leg * f));
    }
}
