package org.y1000.entities.players.event;

import org.y1000.entities.players.MoveAction;
import org.y1000.entities.players.Player;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.*;

import java.util.List;
import java.util.stream.Collectors;

public final class PlayerSnapshot implements I2ClientMessage {

    private final Packet packet;

    private PlayerSnapshot(Packet packet) {
        this.packet = packet;
    }

    private static PlayerSnapshotPacket buildSnapshot(Player player, int elapsed, MoveAction moveAction) {
        var coordinate = player.coordinate();
        CreatureBaseInfoPacket baseInfoSnapshot = CreatureBaseInfoPacket.newBuilder()
                .setY(coordinate.y())
                .setX(coordinate.x())
                .setElapsedMillis(elapsed)
                .setDirection(player.direction().value())
                .setViewName(player.viewName())
                .setId(player.id())
                .build();
        List<PlayerEquipPacket> equipments = player.getEquipments().stream().map(e -> PlayerEquipEvent.toEquipPacket(player, e))
                .collect(Collectors.toList());
        return PlayerSnapshotPacket.newBuilder()
                .setMoveAction(moveAction != null ? moveAction.value() : -1)
                .setBaseInfo(baseInfoSnapshot)
                .setState(player.stateEnum().value())
                .setMale(player.isMale())
                .addAllEquipments(equipments)
                .build();
    }

    public static PlayerSnapshot build(Player player, int elapsed, MoveAction moveAction) {
        return new PlayerSnapshot(Packet.newBuilder()
                .setPlayerSnapshot(buildSnapshot(player, elapsed, moveAction)).build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
