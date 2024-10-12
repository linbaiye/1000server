package org.y1000.message.clientevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.players.Player;

public final class ClientClickInteractabilityEvent extends AbstractClientEvent
        implements ClientSingleInteractEvent {

    private final long npcId;

    private final String clickedInteractability;

    public ClientClickInteractabilityEvent(long npcId,
                                           String clickedInteractability) {
        Validate.notEmpty(clickedInteractability);
        this.npcId = npcId;
        this.clickedInteractability = clickedInteractability;
    }

    @Override
    public long targetId() {
        return npcId;
    }

    @Override
    public void handle(Player player, ActiveEntity entity) {
        if (player != null && entity instanceof InteractableNpc interactableNpc)
            interactableNpc.interact(player, clickedInteractability);
    }
}
