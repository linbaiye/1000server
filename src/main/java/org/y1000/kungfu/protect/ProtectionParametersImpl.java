package org.y1000.kungfu.protect;

import org.y1000.kungfu.ArmorParameters;
import org.y1000.kungfu.FiveSecondsParameters;
import org.y1000.kungfu.KeepParameters;
import org.y1000.kungfu.KungFuSdb;

public final class ProtectionParametersImpl implements ProtectionParameters {
    private final String name;
    private final KungFuSdb kungFuSdb;
    private final KeepParameters keepParameters;
    private final ArmorParameters armorParameters;
    private final FiveSecondsParameters consumeParameters;

    public ProtectionParametersImpl(String name,
                                    KungFuSdb kungFuSdb,
                                    KeepParameters keepParameters,
                                    ArmorParameters armorParameters,
                                    FiveSecondsParameters consumeParameters) {
        this.name = name;
        this.kungFuSdb = kungFuSdb;
        this.keepParameters = keepParameters;
        this.armorParameters = armorParameters;
        this.consumeParameters = consumeParameters;
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
    public int powerToKeep() {
        return keepParameters.powerToKeep();
    }

    @Override
    public int innerPowerToKeep() {
        return keepParameters.innerPowerToKeep();
    }

    @Override
    public int outerPowerToKeep() {
        return keepParameters.outerPowerToKeep();
    }

    @Override
    public int lifeToKeep() {
        return keepParameters.lifeToKeep();
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
    public int innerPowerPer5Seconds() {
        return consumeParameters.innerPowerPer5Seconds();
    }

    @Override
    public int outerPowerPer5Seconds() {
        return consumeParameters.outerPowerPer5Seconds();
    }

    @Override
    public int powerPer5Seconds() {
        return consumeParameters.powerPer5Seconds();
    }

    @Override
    public int lifePer5Seconds() {
        return consumeParameters.lifePer5Seconds();
    }
}
