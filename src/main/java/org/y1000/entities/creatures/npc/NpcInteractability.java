package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;

public interface NpcInteractability {

    String abilityName();

    void interact(Player clicker, InteractableNpc npc);
}
