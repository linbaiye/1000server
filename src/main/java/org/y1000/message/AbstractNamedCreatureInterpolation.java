package org.y1000.message;

import lombok.Getter;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

@Getter
public abstract class AbstractNamedCreatureInterpolation extends AbstractCreatureInterpolation {
    private final String name;
    public AbstractNamedCreatureInterpolation(long id, Coordinate coordinate, State state, Direction direction,
                                              int elapsedMillis,
                                              String name) {
        super(id, coordinate, state, direction, elapsedMillis);
        this.name = name;
    }
}
