package org.y1000.message.clientevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.DyableEquipment;
import org.y1000.item.Dye;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;

public record ClientDyeEvent(int dyedSlot, int dyeSlot) implements ClientEvent {

    public ClientDyeEvent {
        Validate.isTrue(dyeSlot != dyedSlot);
    }

    private void dye(Player player, Dye dye) {
        player.inventory().getItem(dyedSlot, DyableEquipment.class)
                .ifPresent(equipment -> {
                    dye.dye(equipment);
                    player.consumeItem(dyeSlot);
                    player.emitEvent(new UpdateInventorySlotEvent(player, dyedSlot, player.inventory().getItem(dyedSlot)));
                });
    }
    public void handle(Player player) {
        if (player == null) {
            return;
        }
        player.inventory().getStackItem(dyeSlot(), Dye.class)
                .flatMap(stackItem -> stackItem.origin(Dye.class))
                .ifPresent(d -> dye(player, d));
    }
}
