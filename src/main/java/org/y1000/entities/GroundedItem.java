package org.y1000.entities;

import lombok.Builder;
import lombok.Getter;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.GroundedItemInterpolation;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.util.Coordinate;

import java.util.Objects;


public final class GroundedItem implements PhysicalEntity {

    private final Coordinate coordinate;

    private final int x;

    private final int y;

    @Getter
    private final Integer number;

    private int ttl = 3 * 60 * 1000;

    private EntityEventListener listener;
    @Getter
    private final long id;

    @Getter
    private final String name;

    @Builder
    public GroundedItem(long id, String name, Coordinate coordinate, int x, int y, Integer number) {
        this.id = id;
        this.name = name;
        this.coordinate = coordinate;
        this.x = x;
        this.y = y;
        this.number = number;
    }


    @Override
    public long id() {
        return id;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public void update(int delta) {
        ttl -= delta;
        if (ttl <= 0) {
            // listener.OnEvent();
        }
    }


    public boolean canPickAt(Coordinate from) {
        return coordinate().xDistance(from.x()) <= 2 && coordinate().yDistance(from.y()) <= 3;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroundedItem item = (GroundedItem) o;
        return item.id() == id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return GroundedItemInterpolation.builder()
                .name(name)
                .number(number)
                .id(id())
                .coordinate(coordinate)
                .x(x)
                .y(y)
                .build();
    }

    @Override
    public String toString() {
        return "[" + id() + ", " + coordinate + ", " + name + "]";
    }

    @Override
    public void registerEventListener(EntityEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void deregisterEventListener(EntityEventListener listener) {
        if (this.listener == listener)
            this.listener = null;
    }
}
