package org.y1000.entities.creatures;


public abstract class AbstractCreatureState<C extends Creature> implements CreatureState<C> {
    private int elapsedMillis;
    private final int totalMillis;

    public AbstractCreatureState(int totalMillis) {
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
        if (elapsedMillis > totalMillis) {
            elapsedMillis = totalMillis;
        }
        return elapsedMillis >= totalMillis;
    }

    public int totalMillis() {
        return totalMillis;
    }
}

