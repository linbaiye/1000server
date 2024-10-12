package org.y1000.entities.creatures.npc.interactability;

import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.players.Player;

public interface NpcInteractability {

    /**
     * An ability name that a player sees.
     * @return ability name.
     */
    String playerSeeingName();

    void interact(Player clicker, InteractableNpc npc);
}
