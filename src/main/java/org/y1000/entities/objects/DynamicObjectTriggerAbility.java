package org.y1000.entities.objects;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.UpdateInventorySlotMessage;
import org.y1000.item.Item;

public class DynamicObjectTriggerAbility {

    private final String name;
    private final int number;

    private final String sound;

    public DynamicObjectTriggerAbility(String name, int number, String sound) {
        this.name = name;
        this.number = number;
        this.sound = sound;
    }


    public void onPlayerDropItem(DynamicObject object, Player player, int slot) {
        Item item = player.inventory().getItem(slot);
        if (item == null || !item.name().equals(name)) {
            return;
        }
        if (!player.inventory().hasEnough(name, number)) {
            return;
        }
        player.inventory().decrease(slot,number);
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        if (sound != null)
            object.sentEvent(DynamicObjectSoundEvent.of(object, sound));
        object.triggered();
    }
}
