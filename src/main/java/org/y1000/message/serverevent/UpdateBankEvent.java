package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.item.Item;
import org.y1000.message.ValueEnum;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateBankPacket;

public final class UpdateBankEvent extends AbstractPlayerEvent {
    private final Packet packet;

    private UpdateBankEvent(Player player, Type type, int slot, Item item) {
        super(player, true);
        UpdateBankPacket.Builder builder = UpdateBankPacket.newBuilder().setType(type.value());
        if (type == Type.UPDATE) {
            builder.setUpdateSlot(UpdateInventorySlotEvent.toPacket(slot, item));
        } else if (type != Type.CLOSE) {
            throw new IllegalArgumentException();
        }
        packet = Packet.newBuilder().setUpdateBank(builder).build();
    }


    private enum Type implements ValueEnum {
        UPDATE(1),
        CLOSE(2),
        ;
        private final int v;

        Type(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }

    public static UpdateBankEvent close(Player player) {
        return new UpdateBankEvent(player, Type.CLOSE, 0, null);
    }

    public static UpdateBankEvent update(Player player, Bank bank, int bankSlot) {
        Validate.notNull(player);
        Validate.notNull(bank);
        return new UpdateBankEvent(player, Type.UPDATE, bankSlot, bank.getItem(bankSlot));
    }
}
