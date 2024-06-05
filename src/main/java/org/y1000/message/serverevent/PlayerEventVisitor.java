package org.y1000.message.serverevent;

import org.y1000.entities.players.PlayerStandUpState;
import org.y1000.entities.players.event.*;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.GetGroundItemEvent;
import org.y1000.message.PlayerTextEvent;

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

    default void visit(PlayerDropItemEvent event) {

    }

    default void visit(GetGroundItemEvent event) {

    }

    default void visit(PlayerPickedItemEvent event) {

    }

    default void visit(PlayerTextEvent event) {

    }

    default void visit(UpdateInventorySlotEvent event) {

    }

    default void visit(PlayerUnequipEvent event) {

    }

    default void visit(PlayerEquipEvent event) {
    }

    default void visit(PlayerStartTradeEvent event) {

    }

    default void visit(OpenTradeWindowEvent event) {

    }

    default void visit(PlayerToggleKungFuEvent event) {

    }

    default void visit(PlayerSitDownEvent event) {

    }

    default void visit(PlayerStandUpEvent event) {

    }

    default void visit(PlayerCooldownEvent event) {

    }
}