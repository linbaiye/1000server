package org.y1000.kungfu.attack;

import org.y1000.kungfu.KungFuSdb;

public final class AttackKungFuParametersImpl implements AttackKungFuParameters {

    private final String name;

    private final KungFuSdb kungFuSdb;

    public AttackKungFuParametersImpl(String name, KungFuSdb kungFuSdb) {
        this.name = name;
        this.kungFuSdb = kungFuSdb;
    }

    public int powerToSwing() {
        return kungFuSdb.getEMagic(name);
    }

    public int innerPowerToSwing() {
        return kungFuSdb.getEInPower(name);
    }

    public int recovery() {
        return kungFuSdb.getRecovery(name);
    }

    public int outerPowerToSwing() {
        return kungFuSdb.getEOutPower(name);
    }

    public int lifeToSwing() {
        return kungFuSdb.getELife(name);
    }
}
