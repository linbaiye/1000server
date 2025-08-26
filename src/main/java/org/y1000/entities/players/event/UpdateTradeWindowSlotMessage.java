package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateTradeWindowSlotPacket;

public class UpdateTradeWindowSlotMessage extends Abstract2PlayerMessageEvent {

    public UpdateTradeWindowSlotMessage(Player player, Packet packet) {
        super(player, packet);
    }

    private static UpdateTradeWindowSlotMessage create(Player player, int slot, Item item, boolean self) {
        var builder = UpdateTradeWindowSlotPacket.newBuilder()
                .setSelf(self);
        if (item != null)
            builder.setItem(UpdateInventorySlotMessage.toItemPacket(slot, item));
        return new UpdateTradeWindowSlotMessage(player, Packet.newBuilder().setUpdateTradeWindowSlot(builder).build());
    }

    public static UpdateTradeWindowSlotMessage self(Player player, int slot, Item item) {
        return create(player, slot, item, true);
    }

    public static UpdateTradeWindowSlotMessage another(Player player, int slot, Item item) {
        return create(player, slot, item, false);
    }

}
