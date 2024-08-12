package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Collections;
import java.util.Set;

public class TestingTeleport implements Teleport {

    private final int toRealm;
    //private final int toRealm = 19;

    //private final Coordinate toCoordinate = Coordinate.xy(122, 66);
    private final Coordinate toCoordinate;
    //private final Coordinate toCoordinate = Coordinate.xy(500, 500);
    private final Coordinate coordinate;

    private final UnaryAction<PlayerRealmEvent> eventHandler;


    public TestingTeleport(Coordinate to,
            Coordinate pos,
            int toRealm,
            UnaryAction<PlayerRealmEvent> eventHandler) {
        this.eventHandler = eventHandler;
        this.toCoordinate = to;
        this.coordinate = pos;
        this.toRealm = toRealm;
    }

    public static TestingTeleport fox(UnaryAction<PlayerRealmEvent> eventHandler) {
        return  new TestingTeleport( Coordinate.xy(60, 115), Coordinate.xy(97, 44), 19, eventHandler);
    }


    public static TestingTeleport south(UnaryAction<PlayerRealmEvent> eventHandler) {
        return  new TestingTeleport( Coordinate.xy(500, 500), Coordinate.xy(96, 53), 1, eventHandler);
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
