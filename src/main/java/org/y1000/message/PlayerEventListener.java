package org.y1000.message;

import org.y1000.entities.players.event.PlayerEvent;

public interface PlayerEventListener {

    void onEvent(PlayerEvent event);
}
