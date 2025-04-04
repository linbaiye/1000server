package org.y1000.entities.teleport;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractTeleport implements Teleport {

    private final Set<Coordinate> coordinates;

    private final long id;

    private final Coordinate coordinate;

    private final UnaryAction<PlayerRealmEvent> teleportEventHandler;

    private final int toRealm;

    private final Coordinate toCoordinate;

    private final Coordinate rejectCoordinate;

    private final int realmId;

    private final List<TeleportCost> costs;


    public AbstractTeleport(long id,
                            String idName,
                            CreateGateSdb createGateSdb,
                            UnaryAction<PlayerRealmEvent> teleportEventHandler,
                            int realmId,
                            List<TeleportCost> costs) {
        Validate.notNull(idName);
        Validate.notNull(teleportEventHandler);
        Validate.notNull(createGateSdb);
        this.costs = costs != null ? costs : Collections.emptyList();
        this.id = id;
        this.coordinate = parseCoordinate(idName, createGateSdb);
        this.toCoordinate = Coordinate.xy(createGateSdb.getTX(idName), createGateSdb.getTY(idName));
        this.toRealm = createGateSdb.getServerId(idName);
        this.coordinates = parse(idName, coordinate, createGateSdb);
        this.teleportEventHandler = teleportEventHandler;
        Validate.notNull(coordinate);
        Validate.notNull(toCoordinate);
        this.rejectCoordinate = createGateSdb.getEX(idName) != null ? Coordinate.xy(createGateSdb.getEX(idName), createGateSdb.getEY(idName)) : null;
        this.realmId = realmId;
    }

    public long id() {
        return id;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Set<Coordinate> teleportCoordinates() {
        return coordinates;
    }

    @Override
    public void teleport(Player player) {
        if (player == null) {
            return;
        }
        teleportEventHandler.invoke(new RealmTeleportEvent(player, toRealm, toCoordinate, realmId, rejectCoordinate, costs));
    }

    private static Set<Coordinate> parse(String name, Coordinate coordinate, CreateGateSdb gateSdb) {
        int width = gateSdb.getWidth(name);
        if (width <= 1) {
            return Collections.singleton(coordinate);
        }
        var index = width - 1;
        Set<Coordinate> coordinates  = new HashSet<>();
        coordinates.add(coordinate);
        for (int i = -index; i <= index ; i++) {
            for (int j = -index; j <= index; j++) {
                coordinates.add(coordinate.move(i, j));
            }
        }
        return coordinates;
    }

    private static Coordinate parseCoordinate(String idName, CreateGateSdb gateSdb) {
        var coordinate = Coordinate.xy(gateSdb.getX(idName), gateSdb.getY(idName));
        if (!coordinate.equals(Coordinate.Empty)) {
            return coordinate;
        }
        String randomPos = gateSdb.getRandomPos(idName);
        String[] split = randomPos.split(":");
        int total = split.length / 2;
        var index = ThreadLocalRandom.current().nextInt(0, total);
        return Coordinate.xy(Integer.parseInt(split[index * 2]), Integer.parseInt(split[index * 2 + 1]));
    }
}
