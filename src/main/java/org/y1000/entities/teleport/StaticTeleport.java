package org.y1000.entities.teleport;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class StaticTeleport implements Teleport, Entity  {

    private final long id;

    private final Coordinate coordinate;

    private final CreateGateSdb gateSdb;

    private final Set<Coordinate> coordinates;

    private final String idName;

    public StaticTeleport(long id,
                          String idName,
                          Coordinate coordinate,
                          CreateGateSdb gateSdb) {
        this.id = id;
        this.coordinate = coordinate;
        this.gateSdb = gateSdb;
        coordinates = parse(idName, coordinate, gateSdb);
        this.idName = idName;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return null;
    }


    @Override
    public Set<Coordinate> teleportCoordinates() {
        return coordinates;
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

    @Override
    public void teleport(Player player) {

    }
}
