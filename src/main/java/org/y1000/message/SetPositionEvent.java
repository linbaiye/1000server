package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.event.EntityEventVisitor;
import org.y1000.util.Coordinate;

public final class SetPositionEvent extends AbstractPositionEvent {

    public SetPositionEvent(Creature entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate, entity.stateEnum());
    }

    @Override
    protected PositionType getType() {
        return PositionType.SET;
    }


    public static SetPositionEvent of(Creature creature) {
        return new SetPositionEvent(creature, creature.direction(), creature.coordinate());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
