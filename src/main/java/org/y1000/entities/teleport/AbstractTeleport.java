package org.y1000.entities.teleport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractTeleport implements Teleport {

    private final Set<Coordinate> coordinates;

    private final long id;

    private final Coordinate coordinate;

    private final int toRealm;

    private final Coordinate toCoordinate;

    private final Coordinate rejectCoordinate;

    private final int realmId;

    private final List<TeleportCost> costs;

    private final TeleportHandler teleportHandler;

    public AbstractTeleport(long id,
                            String idName,
                            CreateGateSdb createGateSdb,
                            TeleportHandler teleportHandler,
                            int realmId) {
        Validate.notNull(idName);
        Validate.notNull(createGateSdb);
        this.costs = parseCosts(idName, createGateSdb);
        this.id = id;
        this.coordinate = parseCoordinate(idName, createGateSdb);
        this.toCoordinate = Coordinate.xy(createGateSdb.getTX(idName), createGateSdb.getTY(idName));
        this.toRealm = createGateSdb.getServerId(idName);
        this.coordinates = parseCoordinates(idName, coordinate, createGateSdb);
        this.teleportHandler = teleportHandler;
        Validate.notNull(coordinate);
        Validate.notNull(toCoordinate);
        this.rejectCoordinate = createGateSdb.getEX(idName) != null ? Coordinate.xy(createGateSdb.getEX(idName), createGateSdb.getEY(idName)) : null;
        this.realmId = realmId;
        if (!this.costs.isEmpty())
            Validate.isTrue(rejectCoordinate != null);
    }

    public long id() {
        return id;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Set<Coordinate> coordinates() {
        return coordinates;
    }

    @Override
    public void onPlayerEntered(Player player) {
        if (player != null) {
            teleportHandler.onPlayerEnterTeleport(player, this);
        }
    }

    @Override
    public int rejectRealmId() {
        return realmId;
    }

    @Override
    public List<TeleportCost> costs() {
        return this.costs;
    }

    @Override
    public Coordinate rejectCoordinate() {
        return rejectCoordinate;
    }

    @Override
    public Coordinate toCoordinate() {
        return toCoordinate;
    }

    @Override
    public int toRealmId() {
        return toRealm;
    }

    private static List<TeleportCost> parseCosts(String idName, CreateGateSdb createGateSdb) {
        String needItem = createGateSdb.getNeedItem(idName);
        if (StringUtils.isEmpty(needItem)) {
            return Collections.emptyList();
        }
        String[] split = needItem.split(":");
        if (split.length != 2) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ItemCost(split[0].trim(), Integer.parseInt(split[1].trim())));
    }


    private static Set<Coordinate> parseCoordinates(String name, Coordinate coordinate, CreateGateSdb gateSdb) {
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
