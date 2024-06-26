package org.y1000.event.monster;

import org.y1000.event.EntityEventVisitor;

public interface MonsterEventVisitor extends EntityEventVisitor  {
    default void visit(MonsterEvent event) {

    }

}
