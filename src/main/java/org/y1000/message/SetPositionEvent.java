package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.State;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.util.Coordinate;

public class SetPositionEvent extends AbstractPositionEvent {

    private final State state;

    public SetPositionEvent(Creature entity, Direction direction, Coordinate coordinate, State state) {
        super(entity, direction, coordinate);
        this.state = state;
    }

    @Override
    protected MovementType getType() {
        return MovementType.SET;
    }


    public static SetPositionEvent fromPlayer(Player player) {
        return new SetPositionEvent(player, player.direction(), player.coordinate(), player.stateEnum());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
