package org.y1000.realm;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.npc.Npc;
import org.y1000.util.Coordinate;

import java.util.Optional;

public final class EmptyNpcManager implements NpcManager {
    public final static EmptyNpcManager INSTANCE = new EmptyNpcManager();
    private EmptyNpcManager() {}


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
    public void call(String name, ActiveEntity enemy, Coordinate callAt) {

    }
}
