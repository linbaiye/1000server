package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.realm.PlayerEventHandler;

/**
 * This means there is something contradicting to the client's {@code MoveInput},
 * so will rewind the client as well.
 */
public final class PlayerSetPositionAndStateEvent extends AbstractPlayerPositionEvent {

    public PlayerSetPositionAndStateEvent(Player player) {
        super(player);
    }


    @Override
    public void accept(PlayerEventHandler handler) {
        handler.updateAOI(source());
        handler.sendToVisiblePlayersAndSelf(source(), this);
    }

    public static PlayerSetPositionAndStateEvent of(Player player) {
        return new PlayerSetPositionAndStateEvent(player);
    }
}
