package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.OpenTradeWindowPacket;
import org.y1000.network.gen.Packet;

public class OpenTradeWindowMessage extends Abstract2PlayerMessageEvent {

    public OpenTradeWindowMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static OpenTradeWindowMessage proactive(Player player, String anotherName, int slot, long number, String name) {
        OpenTradeWindowPacket packet = OpenTradeWindowPacket.newBuilder()
                .setPassive(false)
                .setSlot(slot)
                .setMaxNumber(number)
                .setItemName(name)
                .setSelfName(player.viewName())
                .setAnotherName(anotherName)
                .build();
        return new OpenTradeWindowMessage(player, Packet.newBuilder().setOpenTradeWindow(packet).build());
    }

    public static OpenTradeWindowMessage passive(Player player, String anotherName) {
        OpenTradeWindowPacket packet = OpenTradeWindowPacket.newBuilder()
                .setPassive(true)
                .setSelfName(player.viewName())
                .setAnotherName(anotherName)
                .build();
        return new OpenTradeWindowMessage(player, Packet.newBuilder().setOpenTradeWindow(packet).build());
    }
}
