package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.interactability.NpcInteractability;
import org.y1000.entities.players.Player;

import java.util.Optional;
import java.util.function.Predicate;


public interface InteractableNpc extends Npc {

    void onClicked(Player player);

    void interact(Player player, String name);

    String shape();

    int avatarImageId();

    String mainMenuDialog();

    Optional<NpcInteractability> findFirstInteractability(Predicate<? super NpcInteractability> predicate);

    default <I extends NpcInteractability> Optional<I> findFirstInteractability(Class<I> type) {
        return findFirstInteractability(npcInteractability -> type.isAssignableFrom(npcInteractability.getClass()))
                .map(type::cast);
    }
}
