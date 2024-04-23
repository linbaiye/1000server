package org.y1000.message.serverevent;

import org.y1000.entities.players.Player;

public abstract class AbstractPlayerEvent extends AbstractEntityEvent {
    public AbstractPlayerEvent(Player source) {
        super(source);
    }

    public Player player() {
        return (Player) source();
    }


    protected abstract void accept(PlayerEventHandler playerEventHandler);


    @Override
    public void accept(EntityEventHandler visitor) {
        if (visitor instanceof PlayerEventHandler playerEventHandler) {
            accept(playerEventHandler);
        }
    }
}
