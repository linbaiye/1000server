package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.Entity;

import java.util.Set;

public interface FilterVisibleEntityEvent extends NpcEvent {
    void filter(Set<Entity> visibleEntities);
}
