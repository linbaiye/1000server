package org.y1000.message.serverevent;

import org.y1000.entities.players.event.*;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.*;

public interface PlayerEventVisitor extends EntityEventVisitor {

    default void visit(JoinedRealmEvent loginMessage) {

    }

    default void visit(InputResponseMessage inputResponseMessage) {

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

    default void visit(PlayerUnequipMessage event) {

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

//    default void visit(RewindEvent event) {
//    }

    default void visit(ItemOrKungFuAttributeEvent event) {

    }
}