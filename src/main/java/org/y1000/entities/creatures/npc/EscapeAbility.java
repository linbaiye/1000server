package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface EscapeAbility {
    Optional<Coordinate> computeSafeSpot(Npc npc, ActiveEntity enemy);

}
