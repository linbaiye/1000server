package org.y1000.message.input;

import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public record MoveInput(Coordinate from, Direction direction) {
}
