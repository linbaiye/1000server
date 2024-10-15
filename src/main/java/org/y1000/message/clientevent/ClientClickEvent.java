package org.y1000.message.clientevent;


import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.creatures.npc.Quester;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.UpdateQuestWindowEvent;

public class ClientClickEvent extends AbstractClientEvent implements ClientSingleInteractEvent {

    private final long clickedId;

    public ClientClickEvent(long clickedId) {
        this.clickedId = clickedId;
    }

    @Override
    public long targetId() {
        return clickedId;
    }

    public void handle(Player source, ActiveEntity clicked) {
        Validate.notNull(source);
        Validate.notNull(clicked);
        if (clicked.id() != clickedId) {
            return;
        }
        if (clicked instanceof Player player) {
            source.emitEvent(PlayerTextEvent.playerClicked(source, player));
        } else if (clicked instanceof Quester quester) {
            source.emitEvent(new UpdateQuestWindowEvent(source, quester));
        } else if (clicked instanceof InteractableNpc interactableNpc) {
            interactableNpc.onClicked(source);
        }
    }
}
