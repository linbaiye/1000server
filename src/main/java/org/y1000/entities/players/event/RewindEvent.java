package org.y1000.entities.players.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractPositionEvent;
import org.y1000.message.PositionType;
import org.y1000.message.SetPositionEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.util.Coordinate;


public final class RewindEvent extends AbstractPositionEvent {
    private RewindEvent(Player source, Direction direction, Coordinate coordinate) {
        super(source, direction, coordinate, source.stateEnum());
    }


    public Player player() {
        return (Player) source();
    }

    @Override
    protected PositionType getType() {
        return PositionType.REWIND;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public SetPositionEvent toSetPosition() {
        return new SetPositionEvent((Creature) source(), direction(), coordinate());
    }

    public static RewindEvent of(Player player) {
        return new RewindEvent(player, player.direction(), player.coordinate());
    }
}
