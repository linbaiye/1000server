package org.y1000.entities.item;

import lombok.Builder;
import org.y1000.entities.Entity;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.util.Coordinate;

@Builder
public class GroundedItem extends AbstractItem implements Entity {

    private final Coordinate coordinate;

    private final int x;

    private final int y;

    private final int number;

    private int ttl = 3 * 60 * 1000;

    private final EntityEventListener listener;

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public void update(int delta) {
        ttl -= delta;
        if (ttl <= 0) {
            listener.OnEvent();
        }
    }

    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return null;
    }
}
