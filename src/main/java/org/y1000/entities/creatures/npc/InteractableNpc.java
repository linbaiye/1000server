package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;


public interface InteractableNpc extends Npc {

    void onClicked(Player player);

    void interact(Player player, String name);

    String shape();

    int avatarImageId();

    String mainMenuDialog();
}
