package org.y1000.realm.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;

public record BroadcastSoundEvent(String sound) implements BroadcastEvent {

    public BroadcastSoundEvent {
        Validate.notNull(sound);
    }

    @Override
    public void send(Player player) {
        if (player != null) {
            player.emitEvent(EntitySoundEvent.broadcast(player, sound));
        }
    }
}
