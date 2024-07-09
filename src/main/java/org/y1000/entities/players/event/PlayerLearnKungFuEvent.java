package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.KungFuPacket;
import org.y1000.network.gen.Packet;

public final class PlayerLearnKungFuEvent extends AbstractPlayerEvent {
    private final int slot;

    private final String kungFuName;

    private final int level;

    private final int kungFuType;

    public PlayerLearnKungFuEvent(Player source, int slot, KungFu kungFu) {
        super(source);
        this.slot = slot;
        this.kungFuName = kungFu.name();
        this.level = kungFu.level();
        this.kungFuType = kungFu.kungFuType().value();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setLearnKungFu(KungFuPacket.newBuilder()
                        .setLevel(level)
                        .setSlot(slot)
                        .setName(kungFuName)
                        .setType(kungFuType)
                ).build();
    }
}
