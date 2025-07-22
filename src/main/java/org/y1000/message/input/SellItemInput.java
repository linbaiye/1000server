package org.y1000.message.input;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcBuyAbility;
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
