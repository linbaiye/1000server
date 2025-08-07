package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;

import java.util.List;

public interface NpcInteractAbility {
    void decorateMenuActions(List<String> menuActions);

    boolean supportsAction(String name);

    void interact(Player player, Npc npc);
}
