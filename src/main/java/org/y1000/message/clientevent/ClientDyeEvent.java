package org.y1000.message.clientevent;

import org.y1000.entities.players.Player;
import org.y1000.item.Dyable;
import org.y1000.item.Dye;
import org.y1000.item.Equipment;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;

public class ClientDyeEvent extends AbstractClientEvent implements ClientSelfInteractEvent {
    private final int dyeSlot;
    private final int dyedSlot;

    public ClientDyeEvent(int dyedSlot, int dyeSlot) {
        this.dyeSlot = dyeSlot;
        this.dyedSlot = dyedSlot;
    }

    private void dye(Player player, Dye dye) {
        player.inventory().getItem(dyedSlot, Equipment.class)
                .flatMap(equipment -> equipment.findAbility(Dyable.class))
                .ifPresent(dyable -> {
                    dye.dye(dyable);
                    player.consumeItem(dyeSlot);
                    player.emitEvent(new UpdateInventorySlotEvent(player, dyedSlot, player.inventory().getItem(dyedSlot)));
                });
    }

    public void handle(Player player) {
        if (player == null) {
            return;
        }
        player.inventory().getStackItem(dyeSlot, Dye.class)
                .flatMap(stackItem -> stackItem.origin(Dye.class))
                .ifPresent(d -> dye(player, d));
    }
}
