package org.y1000.message.input;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.NpcInteractAbility;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;

public record ClickEntityInput(long id) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id;
    }


    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (!(entity instanceof ActiveEntity activeEntity)) {
            return;
        }
        if (activeEntity.isDead())
            return;
        activeEntity.findAbility(NpcInteractAbility.class)
                .ifPresentOrElse(a -> a.interactedBy(player),
                        () -> activeEntity.clickText().ifPresent(text ->
                                player.sendEvent(PlayerTextMessage.bottom(player,text))));
    }
}
