package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.message.clientevent.ClientAttackEvent;

import java.util.Optional;

interface AttackableState extends CreatureState<PlayerImpl> {

    default void attackIfInsight(PlayerImpl player, ClientAttackEvent event) {
        Optional<Entity> insight = player.getRealm().findInsight(player, event.entityId());
        if (insight.isEmpty()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
        } else {
            player.attack(insight.get());
            player.emitEvent(new PlayerAttackEventResponse(player, event, true));
        }
    }
}

