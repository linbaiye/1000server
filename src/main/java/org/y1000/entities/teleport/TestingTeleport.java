package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Collections;
import java.util.Set;

public class TestingTeleport implements Teleport {

    private final int toRealm = 19;
    //private final int toRealm = 19;

    //private final Coordinate toCoordinate = Coordinate.xy(122, 66);
    private final Coordinate toCoordinate = Coordinate.xy(60, 115);
    //private final Coordinate toCoordinate = Coordinate.xy(500, 500);
    private final Coordinate coordinate = Coordinate.xy(97, 44);

    private final UnaryAction<RealmEvent> eventHandler;

    public TestingTeleport(UnaryAction<RealmEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Set<Coordinate> teleportCoordinates() {
        return Collections.singleton(coordinate);
    }

    @Override
    public void teleport(Player player) {
        eventHandler.invoke(new RealmTeleportEvent(player, toRealm, toCoordinate));
    }
}
