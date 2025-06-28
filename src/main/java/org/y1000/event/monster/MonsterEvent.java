package org.y1000.event.monster;

import org.y1000.entities.creatures.monster.Monster;
import org.y1000.event.IEntityEvent;
import org.y1000.event.EntityEventVisitor;

public interface MonsterEvent extends IEntityEvent {
    Monster monster();

    void accept(MonsterEventVisitor visitor);

    @Override
    default void accept(EntityEventVisitor visitor) {
        if (visitor instanceof MonsterEventVisitor monsterEventVisitor) {
            accept(monsterEventVisitor);
        }
    }
}
