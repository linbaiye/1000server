package org.y1000.kungfu.attack;

import org.apache.commons.lang3.Validate;
import org.y1000.kungfu.*;
import org.y1000.persistence.GuildKungFuPo;

import static org.y1000.kungfu.ParameterConstants.INI_MAGIC_DIV_VALUE;

public class AbstractKungFuParameters implements AttackKungFuParameters {
    protected static final int INI_ADD_DAMAGE = 40;
    protected static final int INI_MUL_ATTACKSPEED = 10;
    protected static final int INI_MUL_AVOID = 6;
    protected static final int INI_MUL_RECOVERY = 10;
    protected static final int INI_MUL_DAMAGEBODY = 23;
    protected static final int INI_MUL_DAMAGEHEAD = 17;
    protected static final int INI_MUL_DAMAGEARM = 17;
    protected static final int INI_MUL_DAMAGELEG = 17;
    protected final int recovery;
    protected final int bodyDamage;
    protected final int headDamage;
    protected final int armDamage;
    protected final int legDamage;
    protected final int attackSpeed;
    protected final int avoidance;
    protected final int strikeSound;
    protected final int swingSound;
    protected final String name;
    protected final ArmorParameters armorParameters;
    protected final EventResourceParameters eventResourceParameters;
    protected final int effectColor;
    protected final int icon;

    public AbstractKungFuParameters(String name, KungFuSdb kungFuSdb, ArmorParameters armorParameters,
                                      EventResourceParameters eventResourceParameters) {
        this.name = name;
        this.armorParameters = armorParameters;
        recovery = (120 - kungFuSdb.getRecovery(name)) * INI_MUL_RECOVERY / INI_MAGIC_DIV_VALUE;
        attackSpeed = (120 - kungFuSdb.getAttackSpeed(name)) * INI_MUL_ATTACKSPEED / INI_MAGIC_DIV_VALUE;
        avoidance = kungFuSdb.getAvoidance(name) * INI_MUL_AVOID / INI_MAGIC_DIV_VALUE;
        bodyDamage = (kungFuSdb.getDamageBody(name) + INI_ADD_DAMAGE) * INI_MUL_DAMAGEBODY / INI_MAGIC_DIV_VALUE;
        headDamage = valueOrZero(kungFuSdb.getDamageHead(name), INI_MUL_DAMAGEHEAD);
        armDamage = valueOrZero(kungFuSdb.getDamageArm(name), INI_MUL_DAMAGEARM);
        legDamage = valueOrZero(kungFuSdb.getDamageLeg(name), INI_MUL_DAMAGELEG);
        strikeSound = Integer.parseInt(kungFuSdb.getSoundStrike(name));
        swingSound = Integer.parseInt(kungFuSdb.getSoundSwing(name));
        this.effectColor = kungFuSdb.effectColor(name);
        this.eventResourceParameters = eventResourceParameters;
        this.icon = kungFuSdb.icon(name);
    }

    public AbstractKungFuParameters(GuildKungFuPo provider) {
        Validate.notNull(provider);
        eventResourceParameters = new DefaultEventResourceParameters(provider.getSwingLife(),
                provider.getSwingPower(), provider.getSwingOuterPower(), provider.getSwingInnerPower());
        armorParameters = new DefaultArmorParameters(provider.getBodyArmor(), provider.getHeadArmor(), provider.getArmArmor(), provider.getLegArmor());
        name = provider.getName();
        recovery = (120 - provider.getRecovery()) * INI_MUL_RECOVERY / INI_MAGIC_DIV_VALUE;
        attackSpeed = (120 - provider.getAttackSpeed()) * INI_MUL_ATTACKSPEED / INI_MAGIC_DIV_VALUE;
        avoidance = provider.getAvoid() * INI_MUL_AVOID / INI_MAGIC_DIV_VALUE;
        bodyDamage = (provider.getBodyDamage() + INI_ADD_DAMAGE) * INI_MUL_DAMAGEBODY / INI_MAGIC_DIV_VALUE;
        headDamage = valueOrZero(provider.getHeadDamage(), INI_MUL_DAMAGEHEAD);
        armDamage = valueOrZero(provider.getArmDamage(), INI_MUL_DAMAGEARM);
        legDamage = valueOrZero(provider.getLegDamage(), INI_MUL_DAMAGELEG);
        strikeSound = provider.getStrikeSound();
        swingSound = provider.getSwingSound();
        effectColor = provider.getEffectColor();
        icon = provider.getIcon();
    }

    protected int valueOrZero(int v, int mul) {
        return v == 0 ? 0 :
                (v + INI_ADD_DAMAGE) * mul / INI_MAGIC_DIV_VALUE;
    }

    public int powerToSwing() {
        return eventResourceParameters.power();
    }

    public int innerPowerToSwing() {
        return eventResourceParameters.innerPower();
    }

    public int recovery() {
        return recovery;
    }

    public int outerPowerToSwing() {
        return eventResourceParameters.outerPower();
    }

    public int lifeToSwing() {
        return eventResourceParameters.life();
    }

    @Override
    public int bodyArmor() {
        return armorParameters.bodyArmor();
    }

    @Override
    public int headArmor() {
        return armorParameters.headArmor();
    }

    @Override
    public int armArmor() {
        return armorParameters.armArmor();
    }

    @Override
    public int legArmor() {
        return armorParameters.legArmor();
    }

    @Override
    public int attackSpeed() {
        return attackSpeed;
    }

    @Override
    public int bodyDamage() {
        return bodyDamage;
    }

    @Override
    public int avoidance() {
        return avoidance;
    }

    @Override
    public int headDamage() {
        return headDamage;
    }

    @Override
    public int armDamage() {
        return armDamage;
    }

    @Override
    public int legDamage() {
        return legDamage;
    }

    @Override
    public int strikeSound() {
        return strikeSound;
    }

    @Override
    public int swingSound() {
        return swingSound;
    }

    @Override
    public int effectId() {
        return effectColor;
    }

    @Override
    public int icon() {
        return this.icon;
    }

    @Override
    public String toString() {
        return "AttackKungFuFixedParametersImpl{" +
                "viewName='" + name + '\'' +
                ", recovery=" + recovery +
                ", bodyDamage=" + bodyDamage +
                ", headDamage=" + headDamage +
                ", armDamage=" + armDamage +
                ", legDamage=" + legDamage +
                ", attackSpeed=" + attackSpeed +
                ", avoidance=" + avoidance +
                '}';
    }
}
