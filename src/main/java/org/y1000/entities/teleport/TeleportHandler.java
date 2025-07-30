package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public interface TeleportHandler {

    void teleportPlayerTo(Player player, int realmId, Coordinate toCoordinate);
}
