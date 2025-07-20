package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;

public interface NpcNamedAbility {
    String name();

    void startInteract(Player player);

}
