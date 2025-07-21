package org.y1000.message.input;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcNamedAbility;
import org.y1000.entities.players.Player;

public record ClickNpcAbilityInput(long id, String abilityName) implements EntityInteractInput {

    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (entity instanceof Npc npc)
            npc.findAbility(NpcNamedAbility.class, n -> n.name().equals(abilityName))
                    .ifPresent(npcNamedAbility -> npcNamedAbility.startInteract(player, npc));
    }
}
