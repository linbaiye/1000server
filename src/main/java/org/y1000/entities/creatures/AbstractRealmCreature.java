package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Map;


/**
 * Creatures controlled by the server.
 * @param <C>
 * @param <S>
 */
public abstract class AbstractRealmCreature<C extends AbstractCreature<C,S>, S extends AbstractCreateState<C>> extends AbstractCreature<C, S>{

    private final Coordinate spawnCoordinate;

    private final Rectangle wanderingRange;

    public AbstractRealmCreature(long id,
                                 Coordinate coordinate,
                                 Direction direction,
                                 String name,
                                 Map<State, Integer> stateMillis,
                                 Coordinate spawnCoordinate,
                                 Rectangle wanderingRange) {
        super(id, coordinate, direction, name, stateMillis);
        this.spawnCoordinate = spawnCoordinate;
        this.wanderingRange = wanderingRange;
    }
}
