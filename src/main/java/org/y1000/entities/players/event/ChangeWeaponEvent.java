package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;

public final class ChangeWeaponEvent extends AbstractPlayerEvent {
    private final int slot;

    public ChangeWeaponEvent(Player source, int slot) {
        super(source);
        this.slot = slot;
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return null;
    }
}
