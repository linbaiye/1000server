package org.y1000.message.input;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;

public record DropOnEntityInput(long id, int slot) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id ;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (entity instanceof Player another) {
            player.startTradeWith(another, slot);
        }
    }
}
