package org.y1000.kungfu;

import lombok.Builder;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AssistantKungFu extends AbstractKungFu {

    private final boolean eightDirection;

    private static final Map<Direction, List<Direction>> FIVE_EFFECTED_DIRECTIONS = Map.of(
            Direction.UP_LEFT, List.of(Direction.LEFT, Direction.DOWN_LEFT, Direction.UP, Direction.UP_RIGHT),
            Direction.UP_RIGHT, List.of(Direction.UP_LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN_RIGHT),
            Direction.DOWN_LEFT, List.of(Direction.UP_LEFT, Direction.LEFT, Direction.DOWN, Direction.DOWN_RIGHT),
            Direction.DOWN_RIGHT, List.of(Direction.UP_RIGHT, Direction.RIGHT, Direction.DOWN, Direction.DOWN_LEFT)
            );

    private static final Set<Direction> STRAIGHT = Set.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);

    @Builder
    public AssistantKungFu(String name, int exp, boolean eightDirection) {
        super(name, exp);
        this.eightDirection = eightDirection;
    }

    public Set<Coordinate> affectedCoordinates(Player player) {
        var direction = player.direction();
        var front = player.coordinate().moveBy(direction);
        Set<Coordinate> affected = player.coordinate().neighbours();
        affected.remove(front);
        if (eightDirection) {
            return affected;
        }
        if (STRAIGHT.contains(direction)) {
            Set<Coordinate> neighbours1 = front.neighbours();
            return affected.stream().filter(neighbours1::contains).collect(Collectors.toSet());
        }
        return FIVE_EFFECTED_DIRECTIONS.get(direction).stream()
                .map(player.coordinate()::moveBy)
                .collect(Collectors.toSet());
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.ASSISTANT;
    }

    @Override
    public String description() {
        return getDescriptionBuilder().toString();
    }

    @Override
    public KungFu duplicate() {
        return new AssistantKungFu(name(), 0, eightDirection);
    }
}
