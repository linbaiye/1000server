package org.y1000.kungfu.protect;

import lombok.experimental.SuperBuilder;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.KungFuType;

@SuperBuilder
public class ProtectKungFu extends AbstractKungFu {

    private final ProtectionFixedParameters parameters;
    private static final int INIT_SKILL_DIV_ARMOR = 5000;

    public int bodyArmor() {
        return applyLevelToArmor(parameters.bodyArmor());
    }

    public int headArmor() {
        return applyLevelToArmor(parameters.headArmor());
    }

    public int armArmor() {
        return applyLevelToArmor(parameters.armArmor());
    }

    public String disableSound() {
        return parameters.disableSound();
    }

    public String enableSound() {
        return parameters.enableSound();
    }

    public int legArmor() {
        return applyLevelToArmor(parameters.legArmor());
    }

    private int applyLevelToArmor(int armor) {
        return armor + armor * level() / INIT_SKILL_DIV_ARMOR;
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.PROTECTION;
    }

    @Override
    public String toString() {
        return "Protection {"
                + "level:" + level()  + ","
                + "bodyArmor:" + bodyArmor()  + ","
                + "headArmor:" + headArmor()   + ","
                + "armArmor:" + armArmor()   + ","
                + "legArmor:" + legArmor()   + "}";
    }
}
