package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.objects.DynamicObjectTriggerAbility;
import org.y1000.entities.players.Player;

public record DropOnEntityInput(long id, int slot) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id ;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (player.isDead())
            return;
        if (entity instanceof Player another) {
            player.dropItemOnAnother(another, slot);
        } else if (entity instanceof DynamicObject object) {
            object.findAbility(DynamicObjectTriggerAbility.class)
                    .ifPresent(t -> t.onPlayerDropItem(object, player, slot));
        }
    }
}
