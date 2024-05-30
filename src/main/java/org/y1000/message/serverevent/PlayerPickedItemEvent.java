package org.y1000.message.serverevent;

import org.y1000.entities.GroundedItem;
import org.y1000.item.Item;
import org.y1000.entities.players.Player;

public final class PlayerPickedItemEvent extends UpdateInventorySlotEvent {
    private final GroundedItem groundedItem;

    public PlayerPickedItemEvent(Player player,
                                 int slot, Item item, GroundedItem groundedItem) {
        super(player, slot, item);
        this.groundedItem = groundedItem;
    }

    public GroundedItem groundedItem() {
        return groundedItem;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
