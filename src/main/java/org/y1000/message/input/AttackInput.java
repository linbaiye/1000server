package org.y1000.message.input;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;



public final class AttackInput implements EntityInteractInput {
    private final long id;
    public AttackInput(long id) {
        this.id = id;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (player != null && entity instanceof ActiveEntity activeEntity)
            player.attack(activeEntity);
    }
}
