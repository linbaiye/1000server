package org.y1000.kungfu;

import static org.y1000.kungfu.ParameterConstants.*;

public final class DefaultKeepParameters implements KeepParameters {

    private final int keepPower;
    private final int keepInnerPower;
    private final int keepOuterPower;
    private final int keepLife;

    public DefaultKeepParameters(String name, KungFuSdb kungFuSdb) {
        keepPower = kungFuSdb.getKMagic(name) * INI_MAGIC_DIV_VALUE;
        keepInnerPower = kungFuSdb.getKInPower(name) * INI_MAGIC_DIV_VALUE;
        keepOuterPower = kungFuSdb.getKOutPower(name) * INI_MAGIC_DIV_VALUE;
        keepLife = kungFuSdb.getKLife(name) * INI_MAGIC_DIV_VALUE;
    }

    @Override
    public int powerToKeep() {
        return keepPower;
    }

    @Override
    public int innerPowerToKeep() {
        return keepInnerPower;
    }

    @Override
    public int outerPowerToKeep() {
        return keepOuterPower;
    }

    @Override
    public int lifeToKeep() {
        return keepLife;
    }

    @Override
    public String toString() {
        return "DefaultKeepParameters{" +
                "keepPower=" + keepPower +
                ", keepInnerPower=" + keepInnerPower +
                ", keepOuterPower=" + keepOuterPower +
                ", keepLife=" + keepLife +
                '}';
    }


}
