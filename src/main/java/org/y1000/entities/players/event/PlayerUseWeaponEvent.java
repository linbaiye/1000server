package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerUseWeaponPacket;

public final class PlayerUseWeaponEvent extends AbstractPlayerEvent{
    private final String weaponName;

    public PlayerUseWeaponEvent(Player source,
                                String weaponName) {
        super(source);
        this.weaponName = weaponName;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setUseWeapon(PlayerUseWeaponPacket.newBuilder()
                .setId(source().id())
                .setName(weaponName)
                .build()).build();
    }
}
