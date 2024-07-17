package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;

public abstract class AbstractPlayerEvent extends Abstract2ClientEntityEvent implements PlayerEvent {

    private final boolean selfEvent;

    public AbstractPlayerEvent(Player source) {
        this(source, false);
    }
    public AbstractPlayerEvent(Player source, boolean selfEvent) {
        super(source);
        this.selfEvent = selfEvent;
    }

    public boolean isSelfEvent() {
        return selfEvent;
    }

    public Player player() {
        return (Player) source();
    }
}
