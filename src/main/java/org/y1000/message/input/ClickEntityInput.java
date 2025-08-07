package org.y1000.message.input;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.NpcInteractDialogAbility;
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
        activeEntity.findAbility(NpcInteractDialogAbility.class)
                .ifPresentOrElse(a -> a.interact(player, activeEntity),
                        () -> activeEntity.clickText().ifPresent(text ->
                                player.sendEvent(PlayerTextMessage.bottom(player,text))));
    }
}
