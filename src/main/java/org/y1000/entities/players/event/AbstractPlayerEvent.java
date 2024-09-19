package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;

public abstract class AbstractPlayerEvent extends Abstract2ClientEntityEvent implements PlayerEvent {


    private final Visibility visibility;

    public enum Visibility {

        SELF,

        VISIBLE_PLAYERS,

        SPECIFIC,
    }

    public AbstractPlayerEvent(Player source) {
        this(source, false);
    }
    public AbstractPlayerEvent(Player source, boolean selfEvent) {
        super(source);
        visibility = selfEvent? Visibility.SELF : Visibility.SPECIFIC;
    }

    public AbstractPlayerEvent(Player source, Visibility visibility) {
        super(source);
        this.visibility = visibility;
    }

    public boolean visibleToSelf() {
        return visibility == Visibility.SELF;
    }

    public boolean visibleToPlayers() {
        return visibility == Visibility.VISIBLE_PLAYERS;
    }

    public Player player() {
        return (Player) source();
    }
}
