package org.y1000.message;

import lombok.Getter;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.util.Coordinate;

@Getter
public abstract class AbstractNamedCreatureSnapshot extends AbstractCreatureSnapshot {
    private final String name;
    public AbstractNamedCreatureSnapshot(long id, Coordinate coordinate, OldPlayerStateEnum playerStateEnum, Direction direction,
                                         int elapsedMillis,
                                         String name) {
        super(id, coordinate, playerStateEnum, direction, elapsedMillis);
        this.name = name;
    }

    public AbstractNamedCreatureSnapshot(long id, Coordinate coordinate, int stateValue, Direction direction,
                                         int elapsedMillis,
                                         String name) {
        super(id, coordinate, stateValue, direction, elapsedMillis, -1);
        this.name = name;
    }
}
