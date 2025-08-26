package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.npc.Npc;
import org.y1000.entities.npc.NpcSellAbility;
import org.y1000.entities.players.Player;

public record BuyItemInput(long id, String name, int number) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (entity instanceof Npc npc) {
            npc.findAbility(NpcSellAbility.class)
                    .ifPresent(s -> s.onPlayerBuy(player, npc, name, number));
        }
    }
}
