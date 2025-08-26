package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public interface TeleportEventHandler {

    void teleportTo(Player player, int toReam, Coordinate toCoordinate);

    void announceDungeonOpen(String announcement);

}
