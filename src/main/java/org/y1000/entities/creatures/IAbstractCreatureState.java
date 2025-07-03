package org.y1000.entities.creatures;


import org.y1000.entities.creatures.monster.NpcActionEnum;

public abstract class IAbstractCreatureState<C extends Creature> implements ICreatureState<C> {
    private int elapsedMillis;
    private final int totalMillis;

    public IAbstractCreatureState(int totalMillis) {
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

    @Override
    public NpcActionEnum state() {
        return NpcActionEnum.Idle;
    }
}

