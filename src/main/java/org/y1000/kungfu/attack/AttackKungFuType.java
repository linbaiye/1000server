package org.y1000.kungfu.attack;

import lombok.Getter;
import org.y1000.kungfu.KungFuType;
import org.y1000.message.ValueEnum;

public enum AttackKungFuType implements ValueEnum {

    QUANFA(0, 400, 560, true),

    SWORD(1, 720, 1100, true),

    BLADE(2, 720, 630, true),

    SPEAR(4, 800, 800, true),

    AXE(3, 800, 800, true),

    BOW(5, 600, 600, false),

    THROW(6, 600, 600, false),
    ;

    private final int v;

    private final int below50;

    private final int above50;

    @Getter
    private final boolean melee;


    AttackKungFuType(int v, int below50, int above50, boolean melee) {
        this.v = v;
        this.below50 = below50;
        this.above50 = above50;
        this.melee = melee;
    }

    @Override
    public int value() {
        return v;
    }

    public int below50Millis() {
        return below50;
    }

    public KungFuType toKungFuType() {
        return KungFuType.fromValue(v);
    }

    public int above50Millis() {
        return above50;
    }

    public static AttackKungFuType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
