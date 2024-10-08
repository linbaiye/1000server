package org.y1000.entities.teleport;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;


public final class ItemCost implements TeleportCost {
    private final String requiredItem;
    private final int requiredNumber;

    public ItemCost(String requiredItem,
             int requiredNumber) {
        Validate.isTrue(requiredNumber > 0);
        Validate.notNull(requiredItem);
        this.requiredItem = requiredItem;
        this.requiredNumber = requiredNumber;
    }

    @Override
    public String check(Player player) {
        if (!player.inventory().hasEnough(requiredItem, requiredNumber)) {
            return "需要" + requiredNumber + "个" + requiredItem + "。";
        }
        return null;
    }

    @Override
    public void charge(Player player) {
        if (check(player) != null)
            return;
        int slot = player.inventory().consume(requiredItem, requiredNumber);
        player.emitEvent(UpdateInventorySlotEvent.update(player, slot));
    }
}
