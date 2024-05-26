package org.y1000.message.serverevent;

import org.y1000.entities.GroundedItem;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.Packet;

public final class PlayerPickedItemEvent extends AbstractPlayerEvent  {
    private final int slot;
    private final Item pickedItem;
    private final GroundedItem groundedItem;

    public PlayerPickedItemEvent(Player player,
                                 int slot, Item item, GroundedItem groundedItem) {
        super(player);
        this.slot = slot;
        this.pickedItem = item;
        this.groundedItem = groundedItem;
    }

    public GroundedItem groundedItem() {
        return groundedItem;
    }

    @Override
    protected Packet buildPacket() {
        InventoryItemPacket.Builder builder = InventoryItemPacket.newBuilder()
                .setSlotId(slot)
                .setName(pickedItem.name());
        var number = pickedItem instanceof StackItem stackItem ? stackItem.number() : null;
        if (number != null) {
            builder.setNumber(number);
        }
        return Packet.newBuilder()
                .setUpdateSlot(builder)
                .build();
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
