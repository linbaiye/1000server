package org.y1000.entities.players.event;


import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.Packet;

public final class UpdateInventorySlotMessage extends Abstract2PlayerMessageEvent {

    public UpdateInventorySlotMessage(Player source, Packet packet) {
        super(source, packet);
    }

    private static Packet toPacket(int slot, Item item) {
        InventoryItemPacket.Builder builder = InventoryItemPacket.newBuilder()
                .setSlotId(slot)
                .setColor(item != null ? item.color() : 0)
                .setIcon(item != null ? item.icon() : 0)
                .setName(item != null ? item.name() : "");
        var number = item instanceof StackItem stackItem ? stackItem.number() : null;
        if (number != null) {
            builder.setNumber(number);
        }
        return Packet.newBuilder().setUpdateSlot(builder.build()).build();
    }

    public static UpdateInventorySlotMessage update(Player player, int slotId, Item item) {
        return new UpdateInventorySlotMessage(player, toPacket(slotId, item));
    }

    public static UpdateInventorySlotMessage update(Player player, int slotId) {
        return new UpdateInventorySlotMessage(player, toPacket(slotId, player.inventory().getItem(slotId)));
    }
}
