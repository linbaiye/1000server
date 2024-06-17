package org.y1000.kungfu;

import static org.y1000.kungfu.ParameterConstants.INI_MAGIC_DIV_VALUE;

public class DefaultEventResourceParameters implements EventResourceParameters {

    private static final int INI_MUL_EVENTINPOWER  = 22;
    private static final int INI_MUL_EVENTOUTPOWER  = 22;

    private static final int INI_MUL_EVENTPOWER  = 10;
    private static final int INI_MUL_EVENTLIFE = 8;

    private final int power;
    private final int innerPower;
    private final int outerPower;

    private final int life;

    public DefaultEventResourceParameters(String name, KungFuSdb kungFuSdb) {
        life = kungFuSdb.getELife(name) * INI_MUL_EVENTLIFE / INI_MAGIC_DIV_VALUE;
        power = kungFuSdb.getEMagic(name) * INI_MUL_EVENTPOWER / INI_MAGIC_DIV_VALUE;
        outerPower = kungFuSdb.getEOutPower(name) * INI_MUL_EVENTOUTPOWER / INI_MAGIC_DIV_VALUE;
        innerPower = kungFuSdb.getEInPower(name) * INI_MUL_EVENTINPOWER / INI_MAGIC_DIV_VALUE;
    }


    @Override
    public int power() {
        return power;
    }

    @Override
    public int innerPower() {
        return innerPower;
    }

    @Override
    public int outerPower() {
        return outerPower;
    }

    @Override
    public int life() {
        return life;
    }
}
