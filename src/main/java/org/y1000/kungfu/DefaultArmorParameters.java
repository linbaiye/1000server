package org.y1000.kungfu;

import static org.y1000.kungfu.ParameterConstants.*;

public final class DefaultArmorParameters implements ArmorParameters {

    private static final int INI_MUL_ARMORBODY      = 7;
    private static final int INI_MUL_ARMORHEAD       = 8;
    private static final int INI_MUL_ARMORARM        = 8;
    private static final int INI_MUL_ARMORLEG        = 8;


    private final int bodyArmor;
    private final int headArmor;
    private final int armArmor;
    private final int legArmor;

    public DefaultArmorParameters(String name, KungFuSdb kungFuSdb) {
        bodyArmor = kungFuSdb.getArmorBody(name) *  INI_MUL_ARMORBODY / INI_MAGIC_DIV_VALUE;
        headArmor = kungFuSdb.getArmorHead(name) *  INI_MUL_ARMORHEAD / INI_MAGIC_DIV_VALUE;
        armArmor = kungFuSdb.getArmorArm(name) *  INI_MUL_ARMORARM / INI_MAGIC_DIV_VALUE;
        legArmor = kungFuSdb.getArmorLeg(name) *  INI_MUL_ARMORLEG / INI_MAGIC_DIV_VALUE;
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
