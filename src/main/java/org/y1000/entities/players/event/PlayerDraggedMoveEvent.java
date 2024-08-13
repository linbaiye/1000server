package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.AbstractPositionEvent;
import org.y1000.message.PositionType;
import org.y1000.util.Coordinate;

public final class PlayerDraggedMoveEvent extends AbstractPositionEvent {

    public PlayerDraggedMoveEvent(Creature source, Direction direction, Coordinate coordinate) {
        super(source, direction, coordinate, source.stateEnum());
        Validate.isTrue(source.stateEnum() == State.DIE);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }

    @Override
    protected PositionType getType() {
        return PositionType.DRAGGED;
    }
}
