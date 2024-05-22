package org.y1000.message.serverevent;

import org.y1000.entities.players.event.CharacterChangeWeaponEvent;
import org.y1000.entities.players.event.InventorySlotSwappedEvent;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.message.InputResponseMessage;

public interface PlayerEventVisitor extends EntityEventVisitor {

    default void visit(JoinedRealmEvent loginMessage) {

    }

    default void visit(InputResponseMessage inputResponseMessage) {

    }

    default void visit(PlayerLeftEvent event) {

    }

    default void visit(PlayerAttackEventResponse event) {

    }

    default void visit(PlayerAttackEvent event) {

    }

    default void visit(InventorySlotSwappedEvent event) {

    }

    default void visit(CharacterChangeWeaponEvent event) {

    }
}