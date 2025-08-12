package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;

/**
 * Some event that will be to player(s).
 */
public abstract class AbstractMessagePlayerEvent extends AbstractPlayerEvent implements I2ClientMessage {

    private final Packet packet;

    public AbstractMessagePlayerEvent(Player player, Packet packet) {
        super(player);
        this.packet = packet;
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
