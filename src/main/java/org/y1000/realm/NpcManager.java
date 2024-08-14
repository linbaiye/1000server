package org.y1000.realm;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.realm.event.RealmEvent;

interface NpcManager extends ActiveEntityManager<Npc> {

    NpcManager EMPTY = EmptyNpcManager.INSTANCE;

    void init();

    void handleCrossRealmEvent(RealmEvent crossRealmEvent);
}
