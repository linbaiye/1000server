package org.y1000.entities.creatures;


public abstract class AbstractCreateState<C extends Creature> implements CreatureState<C> {
    private long elapsedMillis;

    public AbstractCreateState() {
        elapsedMillis = 0;
    }

    @Override
    public long elapsedMillis() {
        return elapsedMillis;
    }

    protected void elapse(long delta) {
        elapsedMillis += delta;
    }
    protected void resetElapsedMillis() {
        elapsedMillis = 0;
    }
}
