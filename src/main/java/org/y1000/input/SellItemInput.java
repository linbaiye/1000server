package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.npc.Npc;
import org.y1000.entities.npc.NpcBuyAbility;
import org.y1000.entities.players.Player;

public record SellItemInput(long id, int slot, int number) implements EntityInteractInput {
    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (entity instanceof Npc npc) {
            npc.findAbility(NpcBuyAbility.class).ifPresent(b -> b.onPlayerBuy(player, npc, slot, number));
        }
    }
}
