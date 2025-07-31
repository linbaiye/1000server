package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;

public interface TeleportHandler {

    void onPlayerEnterTeleport(Player player, Teleport teleport);

}
