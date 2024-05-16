package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;

import java.util.Optional;

public interface Realm {
    int STEP_MILLIS = 10;

    void addPlayer(Player player);

    Optional<Entity> findInsight(Entity source, long id);

    RealmMap map();
}
