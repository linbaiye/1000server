package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.serverevent.Visibility;
import org.y1000.network.gen.Packet;

public abstract class IAbstractPlayerEvent implements IPlayerEvent, I2ClientMessage {

    private final Visibility visibility;

    private final Player player;

    private Packet packet;

    public IAbstractPlayerEvent(Player source) {
        this(source, false);
    }

    public IAbstractPlayerEvent(Player source, boolean selfEvent) {
        this.player = source;
        visibility = selfEvent? Visibility.SELF : Visibility.SPECIFIC;
    }

    public IAbstractPlayerEvent(Player source, Visibility visibility) {
        this.player = source;
        this.visibility = visibility;
    }

    @Override
    public Player source() {
        return player;
    }

    public boolean visibleToSelf() {
        return visibility == Visibility.SELF;
    }

    protected abstract Packet buildPacket() ;

    @Override
    public Packet toPacket() {
        if (packet == null) {
            packet = buildPacket();
        }
        return packet;
    }

    public boolean visibleToPlayers() {
        return visibility == Visibility.VISIBLE_PLAYERS;
    }
}
