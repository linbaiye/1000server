package org.y1000.message;

import lombok.AccessLevel;
import lombok.Setter;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.players.MoveAction;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerMoveState;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerInfoPacket;
import org.y1000.network.gen.PlayerInterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public final class PlayerSnapshot extends AbstractCreatureSnapshot {

    @Setter(AccessLevel.PRIVATE)
    private PlayerInfoPacket infoPacket;

    private Packet packet;

    private PlayerSnapshot(long id, Coordinate coordinate, int stateValue, Direction direction, int elapsedMillis, int action) {
        super(id, coordinate, stateValue, direction, elapsedMillis, action);
    }

    @Override
    public Packet toPacket() {
        if (packet != null) {
            return packet;
        }
        packet = Packet.newBuilder()
                .setPlayerInterpolation(PlayerInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setInfo(infoPacket).build())
                .build();
        return packet;
    }

    public static PlayerSnapshot FromPlayer(PlayerImpl player, int elapsedMillis) {
        MoveAction moveAction = null;
        if (player.state() instanceof PlayerMoveState moveState) {
            moveAction = moveState.getMoveAction();
        }
        PlayerSnapshot playerInterpolation = new PlayerSnapshot(player.id(), player.coordinate(),
                player.state().stateEnum().value(), player.direction(),
                elapsedMillis, moveAction != null ? moveAction.value() : -1);
        playerInterpolation.setInfoPacket(PlayerInfo.toPacket(player));
        return playerInterpolation;
    }
}
