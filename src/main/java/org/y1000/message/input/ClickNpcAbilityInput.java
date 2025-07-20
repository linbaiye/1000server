package org.y1000.message.input;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.NpcNamedAbility;
import org.y1000.entities.players.Player;

public record ClickNpcAbilityInput(long id, String abilityName) implements ClientSingleInteractEvent {

    @Override
    public long targetId() {
        return id;
    }

    @Override
    public void handle(Player player, ActiveEntity entity) {
        entity.findAbility(NpcNamedAbility.class)
                .ifPresent(npcNamedAbility -> npcNamedAbility.startInteract(player));
    }
}
