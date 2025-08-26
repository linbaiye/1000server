package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.npc.Npc;
import org.y1000.entities.npc.NpcQuestAbility;
import org.y1000.entities.players.Player;

public record SubmitQuestInput(long id, String questName) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (!(entity instanceof Npc npc))
            return;
        npc.findAbility(NpcQuestAbility.class).ifPresent(i -> {
            if (i.supportsAction(questName))
                i.submit(player, npc, questName);
        });
    }
}
