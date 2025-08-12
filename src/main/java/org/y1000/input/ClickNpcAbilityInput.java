package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcInteractAbility;
import org.y1000.entities.players.Player;

public record ClickNpcAbilityInput(long id, String abilityName) implements EntityInteractInput {

    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (entity instanceof Npc npc)
            npc.findAbility(NpcInteractAbility.class, n -> n.supportsAction(abilityName))
                    .ifPresent(npcInteractAbility -> npcInteractAbility.onAbilityClicked(player, npc, abilityName));
    }
}
