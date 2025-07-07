package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.InventoryPacket;
import org.y1000.network.gen.Packet;

public class InventoryUpdatedEvent extends Abstract2PlayerMessageEvent {
    private InventoryUpdatedEvent(Player player, Packet packet) {
        super(player, packet);
    }


    private static InventoryItemPacket toItem(int slot, Item item) {
        return InventoryItemPacket.newBuilder()
                .setSlotId(slot)
                .setName(item.name())
                .setIcon(item.icon())
                .setNumber((item instanceof StackItem stackItem) ? stackItem.number() : -1)
                .setColor(item.color())
                .build();
    }

    /**
     * The client must pop up the UI to display items.
     * @param player player
     * @return
     */
    public static InventoryUpdatedEvent forceful(Player player) {
        return createInventoryMessage(player, true);
    }

    private static InventoryUpdatedEvent createInventoryMessage(Player player, boolean force) {
        Inventory inventory = player.inventory();
        InventoryPacket.Builder builder = InventoryPacket.newBuilder().setForceful(force);
        inventory.foreach((slot, item) -> builder.addItems(toItem(slot, item)));
        Packet packet = Packet.newBuilder().setInventory(builder.build()).build();
        return new InventoryUpdatedEvent(player, packet);
    }

    /**
     * The client updates the UI if it is popped up.
     * @param player player
     * @return
     */
    public static InventoryUpdatedEvent quiet(Player player) {
        return createInventoryMessage(player, false);
    }
}
