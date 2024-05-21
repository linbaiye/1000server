package org.y1000.realm;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;

import java.util.Optional;

public interface Realm {
    int STEP_MILLIS = 10;

    void addPlayer(Player player);

    Optional<PhysicalEntity> findInsight(PhysicalEntity source, long id);

    RealmMap map();
}
