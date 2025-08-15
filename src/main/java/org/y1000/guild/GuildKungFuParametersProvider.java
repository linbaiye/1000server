package org.y1000.guild;

import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.kungfu.attack.AttackKungFuParameters;

public class GuildKungFuParametersProvider implements AttackKungFuParameters  {
    private final ApplyGuildKungFuInput input;
    private final int icon;
    private final int effectId;
    private final int strikeSound;
    private final int swingSound;

    public GuildKungFuParametersProvider(ApplyGuildKungFuInput input,
                                         int icon,
                                         int effectId,
                                         int strikeSound,
                                         int swingSound) {
        this.input = input;
        this.icon = icon;
        this.effectId = effectId;
        this.strikeSound = strikeSound;
        this.swingSound = swingSound;
    }

    @Override
    public int powerToSwing() {
        return input.getPowerToSwing();
    }

    @Override
    public int innerPowerToSwing() {
        return input.getInnerPowerToSwing();
    }

    @Override
    public int recovery() {
        return input.getRecovery();
    }

    @Override
    public int outerPowerToSwing() {
        return input.getOuterPowerToSwing();
    }

    @Override
    public int lifeToSwing() {
        return input.getLifeToSwing();
    }

    @Override
    public int armArmor() {
        return input.getArmArmor();
    }

    @Override
    public int armDamage() {
        return input.getArmDamage();
    }

    @Override
    public int attackSpeed() {
        return input.getSpeed();
    }

    @Override
    public int avoidance() {
        return input.getAvoid();
    }

    @Override
    public int bodyArmor() {
        return input.getBodyArmor();
    }

    @Override
    public int bodyDamage() {
        return input.getBodyDamage();
    }

    @Override
    public int headArmor() {
        return input.getHeadArmor();
    }

    @Override
    public int effectId() {
        return this.effectId;
    }

    @Override
    public int icon() {
        return this.icon;
    }

    @Override
    public int headDamage() {
        return input.getHeadDamage();
    }

    @Override
    public int legArmor() {
        return input.getLegArmor();
    }

    @Override
    public int legDamage() {
        return input.getLegDamage();
    }

    @Override
    public int strikeSound() {
        return strikeSound;
    }

    @Override
    public int swingSound() {
        return swingSound;
    }

}
