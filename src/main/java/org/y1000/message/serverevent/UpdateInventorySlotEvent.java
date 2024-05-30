package org.y1000.message.serverevent;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.Packet;

public class UpdateInventorySlotEvent extends AbstractPlayerEvent {
    private final int slot;

    private final Item item;

    public UpdateInventorySlotEvent(Player source, int slot, Item item) {
        super(source);
        this.slot = slot;
        this.item = item;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        InventoryItemPacket.Builder builder = InventoryItemPacket.newBuilder()
                .setSlotId(slot)
                .setName(item != null ? item.name() : "");
        var number = item instanceof StackItem stackItem ? stackItem.number() : null;
        if (number != null) {
            builder.setNumber(number);
        }
        return Packet.newBuilder()
                .setUpdateSlot(builder)
                .build();
    }
}
