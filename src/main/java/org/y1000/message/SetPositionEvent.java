package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEventVisitor;
import org.y1000.util.Coordinate;

public final class SetPositionEvent extends AbstractPositionEvent {

    public SetPositionEvent(Player entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate, entity.stateEnum().value());
    }

    public SetPositionEvent(INpc entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate, entity.npcStateEnum().value());
    }

    @Override
    protected PositionType getType() {
        return PositionType.SET;
    }


    public static SetPositionEvent of(Player creature) {
        return new SetPositionEvent(creature, creature.direction(), creature.coordinate());
    }

    public static SetPositionEvent of(INpc creature) {
        return new SetPositionEvent(creature, creature.direction(), creature.coordinate());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
