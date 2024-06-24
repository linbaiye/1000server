package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;

public abstract class AbstractPlayerEvent extends Abstract2ClientEntityEvent implements PlayerEvent {
    public AbstractPlayerEvent(Player source) {
        super(source);
    }

    public Player player() {
        return (Player) source();
    }
}
