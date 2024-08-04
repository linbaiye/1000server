package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.KungFuPacket;
import org.y1000.network.gen.Packet;

public final class UpdateKungFuSlotEvent extends AbstractPlayerEvent {

    private final Packet packet;

    public UpdateKungFuSlotEvent(Player source, int slot, KungFu kungFu) {
        super(source, true);
        KungFuPacket.Builder builder = KungFuPacket.newBuilder()
                .setSlot(slot);
        if (kungFu == null) {
            builder.setType(0);
            builder.setName("");
            builder.setLevel(0);
        } else {
            builder.setType(kungFu.kungFuType().value());
            builder.setName(kungFu.name());
            builder.setLevel(kungFu.level());
        }
        packet = Packet.newBuilder()
                .setUpdateKungFuSlot(builder)
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
