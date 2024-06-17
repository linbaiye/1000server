package org.y1000.kungfu;

import static org.y1000.kungfu.ParameterConstants.*;

public final class DefaultFiveSecondParameters implements FiveSecondsParameters {
    private static final int INI_MUL_5SECENERGY      = 20;
    private static final int INI_MUL_5SECINPOWER     = 14;
    private static final int INI_MUL_5SECOUTPOWER    = 14;
    private static final int INI_MUL_5SECMAGIC       = 9;
    private static final int INI_MUL_5SECLIFE        = 8;

    private final int power;

    private final int outerPower;

    private final int innerPower;

    private final int life;

    public DefaultFiveSecondParameters(String name, KungFuSdb kungFuSdb) {
        outerPower = kungFuSdb.get5OutPower(name) * INI_MUL_5SECOUTPOWER / INI_MAGIC_DIV_VALUE;
        innerPower = kungFuSdb.get5InPower(name) * INI_MUL_5SECINPOWER / INI_MAGIC_DIV_VALUE;
        power = kungFuSdb.get5Magic(name) * INI_MUL_5SECMAGIC / INI_MAGIC_DIV_VALUE;
        life = kungFuSdb.get5Life(name) * INI_MUL_5SECLIFE / INI_MAGIC_DIV_VALUE;
    }


    @Override
    public int innerPowerPer5Seconds() {
        return innerPower;
    }

    @Override
    public int outerPowerPer5Seconds() {
        return outerPower;
    }

    @Override
    public int powerPer5Seconds() {
        return power;
    }

    @Override
    public int lifePer5Seconds() {
        return life;
    }

    @Override
    public String toString() {
        return "DefaultFiveSecondParameters{" +
                "power=" + power +
                ", outerPower=" + outerPower +
                ", innerPower=" + innerPower +
                ", life=" + life +
                '}';
    }

}
