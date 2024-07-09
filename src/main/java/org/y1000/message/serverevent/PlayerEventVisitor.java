package org.y1000.message.serverevent;

import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.players.event.*;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.*;

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

    default void visit(PlayerAttackAoeEvent event) {

    }

    default void visit(PlayerAttributeEvent event) {

    }

    default void visit(PlayerReviveEvent event) {

    }

    default void visit(PlayerGainExpEvent event) {

    }

    default void visit(PlayerMoveEvent moveEvent) {
        visit((AbstractPositionEvent) moveEvent);
    }

    default void visit(RewindEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(PlayerShootEvent event) {
        visit((EntityEvent) event);
    }
}