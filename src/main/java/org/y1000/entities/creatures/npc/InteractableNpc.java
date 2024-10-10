package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;

import java.util.Optional;

public interface InteractableNpc extends Npc {

    void onClicked(Player player);

    void interact(Player player, String name);

    Optional<NpcInteractability> findAbility(String name);

}
