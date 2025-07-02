package org.y1000.message.input;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;

public interface EntityInteractionInput {
    long id();

    void onEntityFound(Player player, Entity entity);
}
