package org.y1000.realm;

import org.y1000.entities.creatures.npc.Npc;

interface NpcManager extends ActiveEntityManager<Npc>, NpcCaller {

    NpcManager EMPTY = EmptyNpcManager.INSTANCE;

    void init();
}
