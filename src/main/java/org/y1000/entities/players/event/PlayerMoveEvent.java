package org.y1000.entities.players.event;

import org.y1000.entities.creatures.npc.event.NpcMoveEvent;
import org.y1000.entities.players.MoveAction;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerMovePacket;


public final class PlayerMoveEvent extends Abstract2VisibleMessageEvent  {

    public PlayerMoveEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerMoveEvent moveBy(Player player,
                                         MoveAction action) {
        PlayerMovePacket playerMovePacket = PlayerMovePacket.newBuilder()
                .setMoveAction(action.value())
                .setMovePacket(NpcMoveEvent.movePacket(player, player.direction()))
                .build();
        Packet packet = Packet.newBuilder().setPlayerMove(playerMovePacket).build();
        return new PlayerMoveEvent(player, packet);
    }
}
