package org.y1000.entities.creatures.npc;

public abstract class AbstractNpcAction implements NpcAction {

    private int actionMillis;
    private int elapsedMillis;

    @Override
    public boolean update(int delta) {
        if (elapsedMillis >= actionMillis)
            return true;
        elapsedMillis += delta;
        return elapsedMillis >= actionMillis;
    }

    @Override
    public int elapsedMillis() {
        return elapsedMillis;
    }

    protected void setTimer(int actionMillis) {
        this.actionMillis = actionMillis;
        elapsedMillis = 0;
    }
}
