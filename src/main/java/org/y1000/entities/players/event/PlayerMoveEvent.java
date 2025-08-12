package org.y1000.entities.players.event;

import org.y1000.entities.npc.event.NpcMoveEvent;
import org.y1000.entities.players.MoveAction;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerMovePacket;
import org.y1000.realm.PlayerEventHandler;


public final class PlayerMoveEvent extends AbstractMessagePlayerEvent {

    private final boolean restore;


    public PlayerMoveEvent(Player player, Packet packet, boolean restore) {
        super(player, packet);
        this.restore = restore;
    }


    private static PlayerMoveEvent create(Player player,
                                          MoveAction action,
                                          int millis,
                                          boolean restore) {
        PlayerMovePacket playerMovePacket = PlayerMovePacket.newBuilder()
                .setMoveAction(action.value())
                .setMovePacket(NpcMoveEvent.forPlayer(player, player.direction()))
                .setStartMillis(millis)
                .build();
        Packet packet = Packet.newBuilder().setPlayerMove(playerMovePacket).build();
        return new PlayerMoveEvent(player, packet, restore);
    }

    public static PlayerMoveEvent restore(Player player,
                                          MoveAction action,
                                          int millis) {
        return create(player, action, millis, true);
    }

    public static PlayerMoveEvent moveBy(Player player,
                                         MoveAction action) {
        return create(player, action, 0, false);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        if (restore)
            handler.sendToVisiblePlayersAndSelf(source(), this);
        else
            handler.sendToVisiblePlayers(source(), this);
    }
}
