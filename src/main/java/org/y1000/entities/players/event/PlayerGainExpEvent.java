package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerGainExpPacket;

public final class PlayerGainExpEvent extends AbstractPlayerEvent {

    private final String name;

    private final int newLevel;

    private final boolean kungFu;

    public PlayerGainExpEvent(Player source, String name, int newLevel) {
        this(source, name, newLevel, true);
    }

    public PlayerGainExpEvent(Player source, String name, int newLevel, boolean kungFu) {
        super(source);
        this.name = name;
        this.newLevel = newLevel;
        this.kungFu = kungFu;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setGainExp(PlayerGainExpPacket.newBuilder()
                        .setKungFu(kungFu)
                        .setName(name)
                        .setLevel(newLevel)
                        .build())
                .build();
    }

    public static PlayerGainExpEvent nonKungFu(Player player, String name) {
        return new PlayerGainExpEvent(player, name, 0, false);
    }
}
