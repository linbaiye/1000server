package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.ViolentCreature;

public interface ViolentNpc extends ViolentCreature, Npc {
    int walkSpeed();
}
