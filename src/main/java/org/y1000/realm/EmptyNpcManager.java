package org.y1000.realm;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEvent;
import org.y1000.realm.event.RealmEvent;

import java.util.Optional;

public final class EmptyNpcManager implements NpcManager {
    public final static EmptyNpcManager INSTANCE = new EmptyNpcManager();
    private EmptyNpcManager() {}

    @Override
    public void onEvent(EntityEvent entityEvent) {

    }

    @Override
    public void update(long delta) {

    }

    @Override
    public Optional<Npc> find(long id) {
        return Optional.empty();
    }

    @Override
    public boolean contains(Npc entity) {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void handleCrossRealmEvent(RealmEvent crossRealmEvent) {

    }
}
