package org.y1000.message.input;

import org.y1000.entities.Entity;
import org.y1000.entities.GroundItem;
import org.y1000.entities.players.Player;

public record PickItemInput(long id) implements EntityInteractInput {

    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (entity instanceof GroundItem groundItem)
            groundItem.pickedBy(player);
    }
}
