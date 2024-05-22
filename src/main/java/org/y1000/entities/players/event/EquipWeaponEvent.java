package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;

public class EquipWeaponEvent extends AbstractPlayerEvent {
    public EquipWeaponEvent(Player source) {
        super(source);
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return null;
    }
}
