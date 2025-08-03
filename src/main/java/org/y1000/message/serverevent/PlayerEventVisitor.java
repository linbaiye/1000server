package org.y1000.message.serverevent;

import org.y1000.entities.players.event.*;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.*;

public interface PlayerEventVisitor extends EntityEventVisitor {

    default void visit(JoinedRealmEvent loginMessage) {

    }

    default void visit(InputResponseMessage inputResponseMessage) {

    }


    default void visit(PlayerTextEvent event) {

    }

    default void visit(UpdateInventorySlotEvent event) {

    }

    default void visit(PlayerUnequipEvent event) {

    }




    default void visit(PlayerCooldownEvent event) {

    }

    default void visit(PlayerAttributeMessage event) {

    }

    default void visit(PlayerReviveEvent event) {

    }

    default void visit(PlayerGainExpEvent event) {

    }


//    default void visit(RewindEvent event) {
//    }

    default void visit(ItemOrKungFuAttributeEvent event) {

    }
}