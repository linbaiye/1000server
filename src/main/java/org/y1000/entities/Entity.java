package org.y1000.entities;

import org.y1000.message.ServerEvent;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Set;

public interface Entity {
    long id();

    Coordinate coordinate();

    default Set<Coordinate> coordinates() {
        return Set.of(coordinate());
    }

    List<ServerEvent> update(long delta);
}
