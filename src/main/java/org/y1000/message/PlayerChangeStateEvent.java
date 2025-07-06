package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerStateEnum;
import org.y1000.entities.players.event.AbstractClientMessageEvent;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerChangeStatePacket;

public final class PlayerChangeStateEvent extends AbstractClientMessageEvent {

    private final boolean includeSelf;

    public PlayerChangeStateEvent(Player player, Packet packet, boolean includeSelf) {
        super(player, packet);
        this.includeSelf = includeSelf;
    }

    private static PlayerChangeStateEvent of(Player player, boolean includeSelf) {
        PlayerStateEnum playerStateEnum = player.state().playerStateEnum();
        PlayerChangeStatePacket changeStatePacket = PlayerChangeStatePacket.newBuilder().setState(playerStateEnum.value())
                .setId(player.id()).setDirection(player.direction().value()).build();
        var packet = Packet.newBuilder().setPlayerChangeState(changeStatePacket).build();
        return new PlayerChangeStateEvent(player, packet, includeSelf);
    }

    public static PlayerChangeStateEvent noSelf(Player player) {
        return of(player, false);
    }

    public static PlayerChangeStateEvent allVisible(Player player) {
        return of(player, true);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        if (includeSelf)
            handler.sendToVisiblePlayersAndSelf(source(), this);
        else
            handler.sendToVisiblePlayers(source(), this);
    }
}
