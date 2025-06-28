package org.y1000.message;

import org.y1000.util.Coordinate;

public abstract class AbstractEntitySnapshot implements I2ClientMessage {

    private final long id;

    private final Coordinate coordinate;

    public AbstractEntitySnapshot(long id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }

    protected long getId() {
        return id;
    }

    protected Coordinate coordinate() {
        return coordinate;
    }
}
