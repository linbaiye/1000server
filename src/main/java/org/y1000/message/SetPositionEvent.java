package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.util.Coordinate;

public class SetPositionEvent extends AbstractPositionEvent {

    public SetPositionEvent(Creature entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate);
    }

    @Override
    protected MovementType getType() {
        return MovementType.SET;
    }


    public static SetPositionEvent ofCreature(Creature creature) {
        return new SetPositionEvent(creature, creature.direction(), creature.coordinate());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
