package org.y1000.entities.players.event;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventHandler;
import org.y1000.network.gen.Packet;

public final class PlayerAttackEvent extends AbstractPlayerEvent {

    private final long hitAt;

    private final Entity target;

    public PlayerAttackEvent(Player source, Entity target, long hitAt) {
        super(source);
        this.hitAt = hitAt;
        this.target = target;
    }

    @Override
    protected Packet buildPacket() {
        return null;
    }

    @Override
    protected void accept(PlayerEventHandler playerEventHandler) {
    }
}
