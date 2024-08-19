package org.y1000.realm;

import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.realm.event.RealmEvent;

import java.util.Set;
import java.util.stream.Collectors;

interface NpcManager extends ActiveEntityManager<Npc> {

    NpcManager EMPTY = EmptyNpcManager.INSTANCE;

    void init();

    void handleCrossRealmEvent(RealmEvent crossRealmEvent);

    default Set<Merchant> findMerchants() {
        return find(npc -> npc instanceof Merchant).stream()
                .map(Merchant.class::cast)
                .collect(Collectors.toSet());
    }
}
