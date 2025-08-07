package org.y1000.entities.creatures.npc;

import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Player;

import java.util.List;

public interface NpcInteractAbility {
    void decorateMenuActions(List<String> menuActions);

    boolean supportsAction(String name);

    void interact(Player player, Npc npc, String abilityName);

    default boolean stateOrDistanceInvalid(Player player, Npc npc) {
        return !npc.canBeSeenAt(player.coordinate()) ||
                player.isDead() || player.isLeftRealm() ||
                npc.findAbility(HurtAbility.class).map(HurtAbility::isDead).orElse(true);
    }
}
