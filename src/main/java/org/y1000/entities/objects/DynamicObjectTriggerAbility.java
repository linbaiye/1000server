package org.y1000.entities.objects;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.entities.players.event.UpdateInventorySlotMessage;
import org.y1000.item.Item;

public class DynamicObjectTriggerAbility {

    private final String name;
    private final int number;

    @Getter
    private boolean triggered;

    public DynamicObjectTriggerAbility(String name, int number) {
        this.name = name;
        this.number = number;
        triggered = false;
    }

    public void onPlayerDropItem(DynamicObject object, Player player, int slot) {
        if (triggered)
            return;
        Item item = player.inventory().getItem(slot);
        if (item == null || !item.name().equals(name)) {
            player.sendEvent(PlayerTextMessage.bottom(player, "使用" + name + "方能开启。"));
            return;
        }
        if (!player.inventory().hasEnough(name, number)) {
            return;
        }
        player.inventory().decrease(slot,number);
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        object.triggered();
        triggered = true;
    }
}
