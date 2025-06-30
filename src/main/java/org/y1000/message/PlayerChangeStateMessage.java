package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerStateEnum;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerChangeStatePacket;

import java.util.Set;
import java.util.stream.Collectors;

public final class PlayerChangeStateMessage extends AbstractPlayerMessage implements SelectablePlayerMessage {

    private final boolean includeSelf;

    public PlayerChangeStateMessage(Player player, Packet packet, boolean includeSelf) {
        super(player, packet);
        this.includeSelf = includeSelf;
    }

    @Override
    public Set<Player> select(Set<Player> players) {
        if (includeSelf) {
            return players.stream().filter(player -> player.canBeSeenAt(source().coordinate())).collect(Collectors.toSet());
        } else {
            return players.stream().filter(player -> player.canBeSeenAt(source().coordinate()) && !player.equals(source())).collect(Collectors.toSet());
        }
    }

    public static PlayerChangeStateMessage of(Player player, boolean includeSelf) {
        PlayerStateEnum playerStateEnum = player.state().playerStateEnum();
        PlayerChangeStatePacket changeStatePacket = PlayerChangeStatePacket.newBuilder().setState(playerStateEnum.value())
                .setId(player.id()).setDirection(player.direction().value()).build();
        var packet = Packet.newBuilder().setPlayerChangeState(changeStatePacket).build();
        return new PlayerChangeStateMessage(player, packet, includeSelf);
    }

    public static PlayerChangeStateMessage of(Player player) {
        return of(player, true);
    }
}
