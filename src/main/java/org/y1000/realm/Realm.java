package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;

import java.util.Optional;

public interface Realm {

    void addPlayer(Player player);

    Optional<Entity> findInsight(Entity source, long id);

    RealmMap map();
}
