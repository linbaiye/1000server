package org.y1000.entities;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.GroundedItemInterpolation;
import org.y1000.util.Coordinate;

import java.util.Objects;
import java.util.Optional;


public final class GroundedItem extends AbstractPhysicalEntity {

    private final Coordinate coordinate;

    @Getter
    private final Integer number;

    private long ttl = 3 * 60 * 1000;

    @Getter
    private final String name;

    private final String dropSound;

    private final String pickSound;

    @Builder
    public GroundedItem(long id, String name, Coordinate coordinate, Integer number, String dropSound, String pickSound) {
        super(id);
        this.name = name;
        this.coordinate = coordinate;
        this.number = number;
        this.dropSound = StringUtils.isEmpty(dropSound) ? null : dropSound;
        this.pickSound = StringUtils.isAllBlank(pickSound) ? null : pickSound;
    }

    public Optional<String> dropSound() {
        return Optional.ofNullable(dropSound);
    }

    public Optional<String> pickSound() {
        return Optional.ofNullable(pickSound);
    }


    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public void update(int delta) {
        if (ttl <= 0) {
            return;
        }
        ttl -= delta;
        if (ttl <= 0) {
            emitEvent(new RemoveEntityEvent(this));
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
                .build();
    }

    @Override
    public String toString() {
        return "[" + id() + ", " + coordinate + ", " + name + "]";
    }
}
