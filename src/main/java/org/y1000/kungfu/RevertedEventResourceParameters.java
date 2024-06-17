package org.y1000.kungfu;

public class RevertedEventResourceParameters implements EventResourceParameters {

    private final int power;

    private final int innerPower;

    private final int outerPower;

    private final int life;

    public RevertedEventResourceParameters(String name, KungFuSdb kungFuSdb) {
        this.life = kungFuSdb.getELife(name) * -1;
        this.outerPower = kungFuSdb.getEOutPower(name) * -1;
        this.innerPower = kungFuSdb.getEInPower(name) * -1;
        this.power = kungFuSdb.getEMagic(name) * -1;
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
