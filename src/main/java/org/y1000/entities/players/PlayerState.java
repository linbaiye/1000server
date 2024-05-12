package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.message.clientevent.ClientAttackEvent;

import java.util.Optional;

public interface PlayerState extends CreatureState<PlayerImpl> {

    default void attackIfHasKungfu(PlayerImpl player, ClientAttackEvent event) {
        Optional<Entity> insight = player.getRealm().findInsight(player, event.entityId());
        if (insight.isEmpty()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false, 0));
            return;
        }
        player.attackKungFu().ifPresent(attackKungFu -> attackKungFu.attack(player, event, insight.get()));
    }
}

