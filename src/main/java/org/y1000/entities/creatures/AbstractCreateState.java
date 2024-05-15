package org.y1000.entities.creatures;


public abstract class AbstractCreateState<C extends Creature> implements CreatureState<C> {
    private int elapsedMillis;
    private final int totalMillis;

    public AbstractCreateState(int totalMillis) {
        this.totalMillis = totalMillis;
        elapsedMillis = 0;
    }

    @Override
    public int elapsedMillis() {
        return elapsedMillis;
    }

    protected void reset() {
        elapsedMillis = 0;
    }

    protected boolean elapse(int delta) {
        elapsedMillis += delta;
        return elapsedMillis >= totalMillis;
    }

    protected int getTotalMillis() {
        return totalMillis;
    }
}

