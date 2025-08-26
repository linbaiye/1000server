package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowBankWindowPacket;

import java.util.ArrayList;
import java.util.List;

public class PlayerShowBankMessage extends Abstract2PlayerMessageEvent {
    public PlayerShowBankMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerShowBankMessage of(Player player, long bankerId, Bank bank) {
        List<InventoryItemPacket> itemPackets = new ArrayList<>();
        bank.foreach((slot, item) -> itemPackets.add(UpdateInventoryMessage.toItem(slot, item)));
        ShowBankWindowPacket packet = ShowBankWindowPacket.newBuilder()
                .setBankerId(bankerId)
                .setCapacity(bank.capacity())
                .setUnlocked(bank.getUnlocked())
                .addAllItems(itemPackets)
                .build();
        return new PlayerShowBankMessage(player, Packet.newBuilder().setShowBank(packet).build());
    }
}
