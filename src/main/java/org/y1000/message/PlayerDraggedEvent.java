package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEventVisitor;

@Deprecated
public final class PlayerDraggedEvent extends AbstractPositionEvent {

    public PlayerDraggedEvent(Player source) {
        super(source, source.direction(), source.coordinate(), source.stateEnum().value());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected PositionType getType() {
        return PositionType.DRAGGED;
    }
}
