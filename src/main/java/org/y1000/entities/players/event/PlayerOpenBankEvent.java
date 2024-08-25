package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Bank;

import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.OpenBankPacket;
import org.y1000.network.gen.Packet;

public final class PlayerOpenBankEvent extends AbstractPlayerEvent {

    private final Packet packet;

    public PlayerOpenBankEvent(Player source, Bank bank) {
        super(source, true);
        OpenBankPacket.Builder builder = OpenBankPacket.newBuilder()
                .setCapacity(bank.capacity())
                .setUnlocked(bank.getUnlocked());
        packet = Packet.newBuilder()
                .setOpenBank(builder)
                .build();
        bank.foreach((index, item) -> builder.addItems(JoinedRealmEvent.toPacket(index, item)));
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
