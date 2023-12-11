package org.y1000.entities.players;

import org.y1000.connection.gen.Packet;

public class IdleInterpolation implements Interpolation {
    @Override
    public State state() {
        return null;
    }

    @Override
    public boolean canMerge(Interpolation interpolation) {
        return false;
    }

    @Override
    public void merge(Interpolation interpolation) {

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
