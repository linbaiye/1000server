package org.y1000.entities.players;

import org.y1000.connection.gen.Packet;

public class IdleInterpolation implements Interpolation {
    @Override
    public State state() {
        return State.IDLE;
    }

    @Override
    public boolean canMerge(Interpolation interpolation) {
        if (interpolation instanceof IdleInterpolation idleInterpolation) {
            return idleInterpolation.
        }
        return false;
    }

    @Override
    public void merge(Interpolation interpolation) {

    }

    @Override
    public int lengthMillis() {
        return 0;
    }

    @Override
    public Packet toPacket() {
        return null;
    }

    @Override
    public long timestamp() {
        return 0;
    }
}
