package org.y1000.kungfu.protect;

import lombok.Builder;
import org.y1000.entities.players.Armor;
import org.y1000.kungfu.AbstractPeriodicalConsumingKungFu;
import org.y1000.kungfu.KungFuType;

import java.util.List;

public final class ProtectKungFu extends AbstractPeriodicalConsumingKungFu {

    private final ProtectionParameters parameters;

    private static final int INIT_SKILL_DIV_ARMOR = 5000;

    private record ArmorMultiplier(int levelStart, int levelEnd, int multiplier) {
        public boolean contains(int skillLevel) {
            return levelEnd >= skillLevel && levelStart <= skillLevel;
        }

        public int multiply(int armor) {
            return armor + multiplier * armor / 100;
        }
    }

    private static final List<ArmorMultiplier> ARMOR_MULTIPLIERS = List.of(
            new ArmorMultiplier(0,4999,0),
            new ArmorMultiplier(5000,6999,5),
            new ArmorMultiplier(7000,7999,6),
            new ArmorMultiplier(8000,8999,8),
            new ArmorMultiplier(9000,9998,10),
            new ArmorMultiplier(9999,9999,12)
    );


    @Builder
    public ProtectKungFu(String name, int exp, ProtectionParameters parameters) {
        super(name, exp, parameters, parameters);
        this.parameters = parameters;
        resetTimer();
    }


    public Armor armor() {
        return new Armor(bodyArmor(), headArmor(), armArmor(), legArmor());
    }

    public int bodyArmor() {
        return computeLevelToArmor(parameters.bodyArmor());
    }

    public int headArmor() {
        return computeLevelToArmor(parameters.headArmor());
    }

    public int armArmor() {
        return computeLevelToArmor(parameters.armArmor());
    }

    public String disableSound() {
        return parameters.disableSound();
    }

    public String enableSound() {
        return parameters.enableSound();
    }

    public int legArmor() {
        return computeLevelToArmor(parameters.legArmor());
    }


    private int computeLevelToArmor(int armor) {
        var val = armor + armor * level() / INIT_SKILL_DIV_ARMOR;
        return ARMOR_MULTIPLIERS.stream()
                .filter(m -> m.contains(level()))
                .findAny()
                .map(m -> m.multiply(val))
                .orElse(val);
    }


    @Override
    public KungFuType kungFuType() {
        return KungFuType.PROTECTION;
    }

    @Override
    public String description() {
        StringBuilder descritionBuilder = getDescriptionBuilder();
        var am = armor();
        var str = String.format("防御力: %d / %d / %d / %d", am.body(), am.head(), am.arm(), am.leg());
        return descritionBuilder.append(str).append("\n").toString();
    }


    @Override
    public String toString() {
        return "Protection {"
                + "level:" + level()  + ","
                + "bodyArmor:" + bodyArmor()  + ","
                + "headArmor:" + headArmor()   + ","
                + "armArmor:" + armArmor()   + ","
                + "legArmor:" + legArmor()   + "}"
                + "5SecLife:" +   parameters.lifePer5Seconds() + "}";
    }
}
