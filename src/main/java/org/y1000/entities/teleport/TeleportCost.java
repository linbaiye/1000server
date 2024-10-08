package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;


public interface TeleportCost {

    void charge(Player player);

    String check(Player player);
}

