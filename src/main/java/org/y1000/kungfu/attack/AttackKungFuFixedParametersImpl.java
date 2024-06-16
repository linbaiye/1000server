package org.y1000.kungfu.attack;

import org.y1000.kungfu.KungFuSdb;

public final class AttackKungFuFixedParametersImpl implements AttackKungFuFixedParameters {

    private final String name;

    private final KungFuSdb kungFuSdb;

    private static final int INI_MUL_EVENTINPOWER  = 22;
    private static final int INI_MUL_EVENTOUTPOWER  = 22;

    private static final int INI_MUL_EVENTPOWER  = 10;
    private static final int INI_MUL_EVENTLIFE = 8;
    private static final int INI_MAGIC_DIV_VALUE = 10;

    private static final int INI_ADD_DAMAGE       = 40;
    private static final int INI_MUL_ATTACKSPEED  = 10;
    private static final int INI_MUL_AVOID          = 6;
    private static final int INI_MUL_RECOVERY        = 10;
    private static final int INI_MUL_DAMAGEBODY      = 23;
    private static final int INI_MUL_DAMAGEHEAD      = 17;
    private static final int INI_MUL_DAMAGEARM       = 17;
    private static final int INI_MUL_DAMAGELEG       = 17;
    private static final int INI_MUL_ARMORBODY      = 7;
    private static final int INI_MUL_ARMORHEAD       = 8;
    private static final int INI_MUL_ARMORARM        = 8;
    private static final int INI_MUL_ARMORLEG        = 8;

    private final int swingPower;
    private final int swingInnerPower;
    private final int swingOuterPower;
    private final int swingLife;
    private final int recovery;

    private final int bodyDamage;

    private final int headDamage;

    private final int armDamage;

    private final int legDamage;

    private final int attackSpeed;

    private final int avoidance;

    private final int bodyArmor;
    private final int headArmor;
    private final int armArmor;
    private final int legArmor;

    private final int strikeSound;

    private final int swingSound;

    public AttackKungFuFixedParametersImpl(String name, KungFuSdb kungFuSdb) {
        this.name = name;
        this.kungFuSdb = kungFuSdb;
        swingLife = kungFuSdb.getELife(name) * INI_MUL_EVENTLIFE / INI_MAGIC_DIV_VALUE;
        swingPower = kungFuSdb.getEMagic(name) * INI_MUL_EVENTPOWER / INI_MAGIC_DIV_VALUE;
        swingOuterPower = kungFuSdb.getEOutPower(name) * INI_MUL_EVENTOUTPOWER / INI_MAGIC_DIV_VALUE;
        swingInnerPower = kungFuSdb.getEInPower(name) * INI_MUL_EVENTINPOWER / INI_MAGIC_DIV_VALUE;
        recovery = (120 - kungFuSdb.getRecovery(name)) * INI_MUL_RECOVERY / INI_MAGIC_DIV_VALUE;
        attackSpeed = (120 - kungFuSdb.getAttackSpeed(name)) * INI_MUL_ATTACKSPEED / INI_MAGIC_DIV_VALUE;
        avoidance = kungFuSdb.getAvoidance(name) * INI_MUL_AVOID / INI_MAGIC_DIV_VALUE;
        bodyDamage = (kungFuSdb.getDamageBody(name) + INI_ADD_DAMAGE) * INI_MUL_DAMAGEBODY / INI_MAGIC_DIV_VALUE;
        headDamage = valueOrZero(kungFuSdb.getDamageHead(name), INI_MUL_DAMAGEHEAD);
        armDamage = valueOrZero(kungFuSdb.getDamageArm(name), INI_MUL_DAMAGEARM);
        legDamage = valueOrZero(kungFuSdb.getDamageLeg(name), INI_MUL_DAMAGELEG);
        bodyArmor = kungFuSdb.getArmorBody(name) *  INI_MUL_ARMORBODY / INI_MAGIC_DIV_VALUE;
        headArmor = kungFuSdb.getArmorHead(name) *  INI_MUL_ARMORHEAD / INI_MAGIC_DIV_VALUE;
        armArmor = kungFuSdb.getArmorArm(name) *  INI_MUL_ARMORARM / INI_MAGIC_DIV_VALUE;
        legArmor = kungFuSdb.getArmorLeg(name) *  INI_MUL_ARMORLEG / INI_MAGIC_DIV_VALUE;
        strikeSound = Integer.parseInt(kungFuSdb.getSoundStrike(name));
        swingSound = Integer.parseInt(kungFuSdb.getSoundSwing(name));
    }


    private int valueOrZero(int v, int mul) {
        return v == 0 ? 0 :
                (v + INI_ADD_DAMAGE ) * mul/ INI_MAGIC_DIV_VALUE;
    }


    public int powerToSwing() {
        return swingPower;
    }

    public int innerPowerToSwing() {
        return swingInnerPower;
    }

    public int recovery() {
        return kungFuSdb.getRecovery(name);
    }

    public int outerPowerToSwing() {
        return swingOuterPower;
    }

    public int lifeToSwing() {
        return swingLife;
    }


    @Override
    public int bodyArmor() {
        return bodyArmor;
    }

    @Override
    public int headArmor() {
        return headArmor;
    }

    @Override
    public int armArmor() {
        return armArmor;
    }

    @Override
    public int legArmor() {
        return legArmor;
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
    public String toString() {
        return "AttackKungFuFixedParametersImpl{" +
                "name='" + name + '\'' +
                ", recovery=" + recovery +
                ", bodyDamage=" + bodyDamage +
                ", headDamage=" + headDamage +
                ", armDamage=" + armDamage +
                ", legDamage=" + legDamage +
                ", attackSpeed=" + attackSpeed +
                ", avoidance=" + avoidance +
                ", bodyArmor=" + bodyArmor +
                ", headArmor=" + headArmor +
                ", armArmor=" + armArmor +
                ", legArmor=" + legArmor +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(new AttackKungFuFixedParametersImpl("无名拳法", KungFuSdb.INSTANCE));
    }
}
