package org.y1000.message;

import org.y1000.entities.players.MoveAction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerMoveState;
import org.y1000.entities.players.event.PlayerEquipEvent;
import org.y1000.network.gen.*;

import java.util.List;
import java.util.stream.Collectors;

public final class PlayerSnapshot implements I2ClientMessage {

    private final Packet packet;

    private PlayerSnapshot(Packet packet) {
        this.packet = packet;
    }

    private static PlayerSnapshotPacket buildSnapshot(Player player) {
        MoveAction moveAction = null;
        if (player.state() instanceof PlayerMoveState moveState) {
            moveAction = moveState.getMoveAction();
        }
        var coordinate = player.coordinate();
        CreatureBaseInfoPacket baseInfoSnapshot = CreatureBaseInfoPacket.newBuilder()
                .setY(coordinate.y())
                .setX(coordinate.x())
                .setElapsedMillis(player.state().elapsedMillis())
                .setDirection(player.direction().value())
                .setId(player.id())
                .build();
        List<PlayerEquipPacket> equipments = player.getEquipments().stream().map(e -> PlayerEquipEvent.toEquipPacket(player, e))
                .collect(Collectors.toList());
        return PlayerSnapshotPacket.newBuilder()
                .setMoveAction(moveAction != null ? moveAction.value() : -1)
                .setBaseInfo(baseInfoSnapshot)
                .setState(player.state().playerStateEnum().value())
                .setMale(player.isMale())
                .addAllEquipments(equipments)
                .build();
    }

    public static PlayerSnapshot FromPlayer(PlayerImpl player) {
        return new PlayerSnapshot(Packet.newBuilder()
                .setPlayerSnapshot(buildSnapshot(player)).build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
