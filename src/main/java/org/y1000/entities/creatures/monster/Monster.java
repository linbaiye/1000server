package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.npc.ViolentNpc;

import java.util.Optional;

public interface Monster extends ViolentNpc {

    Optional<String> normalSound();

    int escapeLife();

}
