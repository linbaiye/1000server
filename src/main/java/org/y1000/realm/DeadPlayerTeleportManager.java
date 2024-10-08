package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.UnaryAction;

interface DeadPlayerTeleportManager {

    void setTeleportHandler(UnaryAction<RealmTeleportEvent> teleportHandler);

    void onPlayerDead(Player player);

    void update(long delta);
}
