package org.y1000.realm;

import org.y1000.entities.players.event.PlayerEvent;

public interface PlayerEventListener {

    void onEvent(PlayerEvent event);
}
