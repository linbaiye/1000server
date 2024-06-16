package org.y1000.kungfu.protect;

import org.y1000.kungfu.KungFuSdb;

public final class ProtectionFixedParametersImpl implements ProtectionFixedParameters {
    private static final int INI_MUL_ARMORBODY      = 7;
    private static final int INI_MUL_ARMORHEAD       = 8;
    private static final int INI_MUL_ARMORARM        = 8;
    private static final int INI_MUL_ARMORLEG        = 8;
    private static final int INI_MAGIC_DIV_VALUE = 10;
    private final int bodyArmor;
    private final int headArmor;
    private final int armArmor;
    private final int legArmor;

    private final String name;

    private final KungFuSdb kungFuSdb;

    public ProtectionFixedParametersImpl(String name,
                                         KungFuSdb kungFuSdb) {
        this.name = name;
        this.kungFuSdb = kungFuSdb;
        bodyArmor = kungFuSdb.getArmorBody(name) *  INI_MUL_ARMORBODY / INI_MAGIC_DIV_VALUE;
        headArmor = kungFuSdb.getArmorHead(name) *  INI_MUL_ARMORHEAD / INI_MAGIC_DIV_VALUE;
        armArmor = kungFuSdb.getArmorArm(name) *  INI_MUL_ARMORARM / INI_MAGIC_DIV_VALUE;
        legArmor = kungFuSdb.getArmorLeg(name) *  INI_MUL_ARMORLEG / INI_MAGIC_DIV_VALUE;
    }

    @Override
    public String enableSound() {
        return kungFuSdb.getSoundStart(name);
    }

    @Override
    public String disableSound() {
        return kungFuSdb.getSoundEnd(name);
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
}
