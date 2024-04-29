package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.AbstractSerializableEntityEvent;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.message.serverevent.PlayerEventVisitor;

public abstract class AbstractPlayerEvent extends AbstractSerializableEntityEvent {
    public AbstractPlayerEvent(Player source) {
        super(source);
    }

    public Player player() {
        return (Player) source();
    }


    protected abstract void accept(PlayerEventVisitor playerEventHandler);


    @Override
    public void accept(EntityEventVisitor visitor) {
        if (visitor instanceof PlayerEventVisitor playerEventHandler) {
            accept(playerEventHandler);
        }
    }
}
