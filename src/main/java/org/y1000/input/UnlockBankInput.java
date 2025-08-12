package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.npc.Npc;
import org.y1000.entities.npc.NpcBankAbility;
import org.y1000.entities.players.Player;

public record UnlockBankInput(long id) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (!(entity instanceof Npc npc))
            return;
        npc.findAbility(NpcBankAbility.class)
                .ifPresent(npcBankAbility -> npcBankAbility.unlock(npc, player));
    }
}
