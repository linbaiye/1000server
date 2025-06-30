package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

public abstract class AbstractPlayerMessage implements PlayerMessage {
    private final Player player;

    private final Packet packet;

    public AbstractPlayerMessage(Player player, Packet packet) {
        this.player = player;
        this.packet = packet;
    }

    @Override
    public Packet toPacket() {
        return packet;
    }

    @Override
    public Player source() {
        return player;
    }
}
