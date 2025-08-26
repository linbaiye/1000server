package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerStateEnum;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerChangeStatePacket;

public final class PlayerChangeStateEvent extends AbstractMessagePlayerEvent {

    @Getter
    private final boolean includeSelf;

    private final boolean isDead;

    private PlayerChangeStateEvent(Player player, Packet packet, boolean includeSelf, boolean isDead) {
        super(player, packet);
        this.includeSelf = includeSelf;
        this.isDead = isDead;
    }

    private static PlayerChangeStateEvent of(Player player,
                                             boolean includeSelf) {
        PlayerStateEnum playerStateEnum = player.stateEnum();
        PlayerChangeStatePacket changeStatePacket = PlayerChangeStatePacket.newBuilder().setState(playerStateEnum.value())
                .setId(player.id())
                .setX(player.coordinate().x())
                .setY(player.coordinate().y())
                .setDirection(player.direction().value()).build();
        var packet = Packet.newBuilder().setPlayerChangeState(changeStatePacket).build();
        return new PlayerChangeStateEvent(player, packet, includeSelf, player.isDead());
    }

    public static PlayerChangeStateEvent noSelf(Player player) {
        return of(player, false);
    }

    public static PlayerChangeStateEvent allVisible(Player player) {
        return of(player, true);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        if (isDead) {
            handler.onPlayerDead(source(), this);
            return;
        }
        if (includeSelf)
            handler.sendToVisiblePlayersAndSelf(source(), this);
        else
            handler.sendToVisiblePlayers(source(), this);
    }
}
